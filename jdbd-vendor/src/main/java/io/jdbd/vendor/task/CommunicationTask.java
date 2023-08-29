package io.jdbd.vendor.task;

import io.jdbd.session.SessionCloseException;
import io.jdbd.vendor.util.JdbdCollections;
import io.jdbd.vendor.util.JdbdExceptions;
import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @see CommunicationTaskExecutor
 * @see ConnectionTask
 */
public abstract class CommunicationTask {

    private final ITaskAdjutant adjutant;

    private final Consumer<Throwable> errorConsumer;

    protected Publisher<ByteBuf> packetPublisher;

    protected List<Throwable> errorList;

    private TaskPhase taskPhase;

    private TaskSignal taskSignal;

    private MethodStack methodStack;

    private TaskDecodeException decodeException;


    protected CommunicationTask(ITaskAdjutant adjutant, Consumer<Throwable> errorConsumer) {
        this.adjutant = adjutant;
        this.errorConsumer = errorConsumer;
    }

    /**
     * <p>
     * If use this constructor ,then must override {@link #emitError(Throwable)}
     * </p>
     */
    protected CommunicationTask(ITaskAdjutant adjutant) {
        this.adjutant = adjutant;
        this.errorConsumer = this::emitError;
    }


    /**
     * <p>
     * {@link CommunicationTaskExecutor} invoke this method start task.
     * </p>
     *
     * @return <ul>
     * <li>if non-null {@link CommunicationTaskExecutor} will send this {@link Publisher}.</li>
     * <li>if null {@link CommunicationTaskExecutor} immediately invoke {@link #decodeMessage(ByteBuf, Consumer)}</li>
     * </ul>
     */
    @Nullable
    final Publisher<ByteBuf> startTask(TaskSignal signal) {
        if (this.taskPhase != TaskPhase.SUBMITTED) {
            throw createTaskPhaseException(TaskPhase.SUBMITTED);
        }
        this.taskSignal = Objects.requireNonNull(signal, "signal");
        this.taskPhase = TaskPhase.STARTED;
        this.methodStack = MethodStack.START;

        Publisher<ByteBuf> publisher;

        try {
            publisher = start();
        } finally {
            this.methodStack = null;
        }
        return publisher;
    }


    /**
     * <p>
     * {@link CommunicationTaskExecutor} invoke this method read response from database server.
     * </p>
     *
     * @return true : task end.
     */
    final boolean decodeMessage(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer)
            throws TaskStatusException {
        if (this.taskPhase != TaskPhase.STARTED) {
            throw createTaskPhaseException(TaskPhase.STARTED);
        }
        boolean taskEnd;
        final int oldReaderIndex = cumulateBuffer.readerIndex();
        this.methodStack = MethodStack.DECODE;
        try {
            if (this.decodeException == null) {
                taskEnd = decode(cumulateBuffer, serverStatusConsumer);
            } else {
                taskEnd = skipPacketsOnError(cumulateBuffer, serverStatusConsumer);
            }

        } catch (Throwable e) {
            if (JdbdExceptions.isJvmFatal(e)) {
                addError(e);
                publishError(this.errorConsumer);
                throw e;
            }
            if (this.decodeException == null) {
                String m = String.format("Task[%s] decode() method throw exception.", this);
                this.decodeException = new TaskDecodeException(m, e);
                addError(e);
                cumulateBuffer.markReaderIndex();
                cumulateBuffer.readerIndex(oldReaderIndex);
                taskEnd = internalSkipPacketsOnError(cumulateBuffer, serverStatusConsumer);
            } else {
                addError(e);
                publishError(this.errorConsumer);

                throw new TaskStatusException("decode(ByteBuf, Consumer<Object>) method throw exception.",
                        JdbdExceptions.createException(this.errorList));
            }
        } finally {
            this.methodStack = null;
        }
        if (taskEnd) {
            this.taskPhase = TaskPhase.END;
            if (this.decodeException != null) {
                publishError(this.errorConsumer);
            }
        }
        return taskEnd;
    }

    /**
     * <p>
     * This is package method ,{@link CommunicationTaskExecutor} invoke this method get more send packet.
     * </p>
     *
     * @return if non-null {@link CommunicationTaskExecutor} will send this {@link Publisher}.
     */
    @Nullable
    final Publisher<ByteBuf> moreSendPacket() {
        final Publisher<ByteBuf> publisher = this.packetPublisher;
        if (publisher != null) {
            this.packetPublisher = null;
        }
        return publisher;
    }


    /**
     * <p>
     * when network channel close,{@link CommunicationTaskExecutor} invoke this method.
     * </p>
     */
    final void channelCloseEvent() {
        this.taskPhase = TaskPhase.END;
        onChannelClose();
    }


    @Nullable
    final TaskPhase getTaskPhase() {
        return this.taskPhase;
    }


    /**
     * <p>
     * sub class invoke this method submit task to {@link CommunicationTaskExecutor}
     * </p>
     */
    protected final void submit(Consumer<Throwable> consumer) {
        if (this.adjutant.inEventLoop()) {
            syncSubmitTask(consumer);
        } else {
            this.adjutant.execute(() -> syncSubmitTask(consumer));
        }
    }

    protected final void resume(Consumer<Throwable> consumer) {
        if (!this.adjutant.inEventLoop()) {
            consumer.accept(new IllegalStateException("not in EventLoop ,reject resume"));
        } else if (this.taskPhase != TaskPhase.END) {
            consumer.accept(new IllegalStateException("not end ,reject resume"));
        } else {
            this.taskPhase = null;
            syncSubmitTask(consumer);
        }
    }


    /**
     * <p>
     * {@link CommunicationTaskExecutor} invoke this method handle error,when below situation:
     *     <ul>
     *         <li>occur network error: ignore return {@link Action}</li>
     *         <li>packet send failure: handle return {@link Action}</li>
     *         <li>
     *             <ol>
     *                  <li>{@link #decodeMessage(ByteBuf, Consumer)} throw {@link TaskStatusException}:
     *                  ignore return {@link Action} and invoke {@link #moreSendPacket()}
     *                  after {@link CommunicationTaskExecutor#clearChannel} return true.</li>
     *                  <li>{@link #decodeMessage(ByteBuf, Consumer)} return true, but cumulateBuffer {@link ByteBuf#isReadable()}:ignore return {@link Action}</li>
     *             </ol>
     *         </li>
     *     </ul>
     * </p>
     *
     * @return <ul>
     *     <li>{@link Action#MORE_SEND_AND_END} :task end after {@link CommunicationTaskExecutor} invoke {@link #moreSendPacket()} and sent</li>
     *      <li>{@link Action#TASK_END} :task immediately end </li>
     * </ul>
     */
    final Action errorEvent(Throwable e) {
        if (this.taskPhase == TaskPhase.END) {
            return Action.TASK_END;
        }
        this.taskPhase = TaskPhase.END;
        this.methodStack = MethodStack.ERROR;

        Action action;
        action = onError(e);

        this.methodStack = null;

        return action;
    }


    /**
     * @return true : task end.
     */
    protected boolean skipPacketsOnError(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
        throw new UnsupportedOperationException();
    }


    /**
     * for {@link CommunicationTask#CommunicationTask(ITaskAdjutant)}
     */
    protected void emitError(Throwable e) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * this method is useful,when receive params from application(or persistent framework) developer
     * and  execute statement after prepare statement.
     * </p>
     *
     * @param endTask true : task end after invoke {@link #moreSendPacket()}.
     * @return <ul>
     * <li>emit success when {@link CommunicationTaskExecutor} accept signal</li>
     * <li>emit {@link Throwable} when {@link CommunicationTaskExecutor} reject signal</li>
     * </ul>
     */
    @Deprecated
    protected final Mono<Boolean> sendPacketSignal(boolean endTask) {
        final Mono<Boolean> mono;
        if (this.adjutant.inEventLoop()) {
            if (this.methodStack == MethodStack.DECODE) {
                mono = Mono.just(Boolean.TRUE);
            } else if (this.methodStack != null) {
                mono = Mono.error(new IllegalStateException
                        (String.format("Unsupported invoke in %s method stack.", this.methodStack)));
            } else {
                mono = this.taskSignal.sendPacket(this, endTask)
                        .then(Mono.just(Boolean.FALSE));
            }
        } else {
            mono = this.taskSignal.sendPacket(this, endTask)
                    .then(Mono.just(Boolean.FALSE));
        }
        return mono;
    }

    protected final boolean inDecodeMethod() {
        return this.adjutant.inEventLoop() && this.methodStack == MethodStack.DECODE;
    }


    /**
     * <p>
     * for {@link Consumer} interface.
     * </p>
     */
    protected final void sendPacket(Publisher<ByteBuf> publisher) {
        this.packetPublisher = publisher;
    }


    public final boolean hasError() {
        List<Throwable> errorList = this.errorList;
        return errorList != null && errorList.size() > 0;
    }

    public final boolean containsError(Class<? extends Throwable> errorType) {
        List<Throwable> errorList = this.errorList;
        boolean contains = false;
        if (errorList != null) {
            for (Throwable error : errorList) {
                if (errorType.isAssignableFrom(error.getClass())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    protected final void addError(Throwable error) {
        List<Throwable> errorList = this.errorList;
        if (errorList == null) {
            this.errorList = errorList = JdbdCollections.arrayList();
        }
        errorList.add(JdbdExceptions.wrapIfNonJvmFatal(error));
    }


    /**
     * @param errorConsumer <ul>
     *                      <li>{@link reactor.core.publisher.MonoSink#error(Throwable)}</li>
     *                      <li>{@link reactor.core.publisher.FluxSink#error(Throwable)}</li>
     *                      <li>other</li>
     *                      </ul>
     */
    protected final void publishError(Consumer<Throwable> errorConsumer) {
        final List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            throw new IllegalStateException("No error,cannot publish error.");
        }
        errorConsumer.accept(JdbdExceptions.createException(errorList));
    }


    /**
     * @see #channelCloseEvent()
     */
    protected void onChannelClose() {
        if (!containsError(SessionCloseException.class)) {
            addError(new SessionCloseException("Session unexpectedly close"));
            publishError(this.errorConsumer);
        }
    }

    /**
     * <p>
     * this method shouldn't throw {@link Throwable} ,because that will cause network channel close.
     * </p>
     */
    protected abstract Action onError(Throwable e);


    /**
     * <p>
     * this method shouldn't throw {@link Throwable} ,because that will cause network channel close.
     * </p>
     */
    @Nullable
    protected abstract Publisher<ByteBuf> start();


    /**
     * <p>
     * this method shouldn't throw {@link Throwable} ,that mean bug.
     * But if this method throw {@link Throwable} ,{@link CommunicationTaskExecutor} will invoke
     * {@link CommunicationTaskExecutor#clearChannel(ByteBuf, Class)} clear network channel,
     *
     * </p>
     *
     * @return true ,task end.
     */
    protected abstract boolean decode(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer);


    /*################################## blow private method ##################################*/

    /**
     * @return true : task end.
     */
    private boolean internalSkipPacketsOnError(ByteBuf cumulateBuffer, Consumer<Object> serverStatusConsumer) {
        try {
            return skipPacketsOnError(cumulateBuffer, serverStatusConsumer);
        } catch (Throwable e) {
            addError(e);
            publishError(this.errorConsumer);
            throw new TaskStatusException("decode(ByteBuf, Consumer<Object>) method throw exception.",
                    JdbdExceptions.createException(this.errorList));
        }
    }

    /**
     * @see #submit(Consumer)
     */
    private void syncSubmitTask(final Consumer<Throwable> consumer) {
        if (this.taskPhase == null) {
            try {
                this.adjutant.syncSubmitTask(this, this::updateSubmitResult);
            } catch (Throwable e) {
                consumer.accept(e);
            }
        } else {
            consumer.accept(new IllegalStateException("Communication task have submitted."));
        }
    }


    private void updateSubmitResult() {
        if (this.taskPhase != null) {
            throw new IllegalStateException(String.format("this.taskPhase[%s] isn't null", this.taskPhase));
        }
        this.taskPhase = TaskPhase.SUBMITTED;
    }


    private IllegalStateException createTaskPhaseException(TaskPhase expect) {
        String message = String.format("%s TaskPhase[%s] isn't %s", this, this.taskPhase, expect);
        return new IllegalStateException(message);
    }


    protected enum Action {
        MORE_SEND_AND_END,
        TASK_END
    }


    enum TaskPhase {
        SUBMITTED,
        STARTED,
        END
    }

    private enum MethodStack {
        START,
        DECODE,
        ERROR,
    }


    private static final class TaskDecodeException extends RuntimeException {

        private TaskDecodeException(String message, Throwable cause) {
            super(message, cause);
        }

    }


}
