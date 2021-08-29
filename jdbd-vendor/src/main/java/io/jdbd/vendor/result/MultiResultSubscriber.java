package io.jdbd.vendor.result;

import io.jdbd.JdbdException;
import io.jdbd.result.*;
import io.jdbd.stmt.ResultType;
import io.jdbd.stmt.SubscribeException;
import io.jdbd.vendor.task.ITaskAdjutant;
import io.jdbd.vendor.util.JdbdExceptions;
import io.jdbd.vendor.util.JdbdFunctions;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @see FluxResult
 */
final class MultiResultSubscriber extends AbstractResultSubscriber {


    static MultiResult create(ITaskAdjutant adjutant, Consumer<FluxResultSink> callback) {
        final FluxResult result = FluxResult.create(sink -> {
            try {
                callback.accept(sink);
            } catch (Throwable e) {
                sink.error(JdbdExceptions.wrap(e));
            }
        });
        return new ReactorMultiResultImpl(adjutant, result);
    }

    private final Publisher<Result> source;

    private final Queue<Result> resultQueue = new LinkedList<>();

    private final Queue<SinkWrapper> sinkQueue = new LinkedList<>();

    private Subscription subscription;

    private boolean done;


    private MultiResultSubscriber(Publisher<Result> source) {
        this.source = source;
    }


    @Override
    public final void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(Long.MAX_VALUE);
    }

    @Override
    public final boolean isCancelled() {
        return hasError();
    }

    @Override
    public final void onNext(final Result result) {
        // this method invoker in EventLoop
        if (!hasError()) {
            this.resultQueue.offer(result);
            drainResult();
        }
    }

    @Override
    public final void onError(Throwable t) {
        // this method invoker in EventLoop
        if (!hasError()) {
            drainResult();
        }
        addError(t);
        drainError();
    }

    @Override
    public final void onComplete() {
        // this method invoker in EventLoop
        this.done = true;
        if (hasError()) {
            drainError();
        } else {
            drainResult();
        }
    }

    @Override
    final ResultType getSubscribeType() {
        return ResultType.MULTI_RESULT;
    }


    private void drainError() {
        final List<Throwable> errorList = this.errorList;
        if (errorList == null || errorList.isEmpty()) {
            throw new IllegalStateException("No error");
        }
        final Queue<SinkWrapper> sinkQueue = this.sinkQueue;
        if (!sinkQueue.isEmpty()) {
            final JdbdException error = JdbdExceptions.createException(errorList);
            SinkWrapper sink;
            while ((sink = sinkQueue.poll()) != null) {
                sink.error(error);
            }
        }
        if (!this.resultQueue.isEmpty()) {
            this.resultQueue.clear();
        }
    }


    private void drainResult() {
        if (hasError()) {
            throw new IllegalStateException("has error ,reject drain result.");
        }
        final Queue<SinkWrapper> sinkQueue = this.sinkQueue;
        final Queue<Result> resultQueue = this.resultQueue;

        SinkWrapper sink;
        Result currentResult;
        ResultType nonExpectedType = null;

        while ((sink = sinkQueue.peek()) != null) {
            currentResult = resultQueue.poll();
            if (currentResult == null) {
                break;
            }
            if (currentResult instanceof ResultRow) {
                final FluxSink<ResultRow> fluxSink = sink.fluxSink;
                if (fluxSink == null) {
                    nonExpectedType = ResultType.QUERY;
                    break;
                } else {
                    fluxSink.next((ResultRow) currentResult);
                }
            } else if (currentResult instanceof ResultState) {
                final ResultState state = (ResultState) currentResult;
                sinkQueue.poll();
                if (state.hasColumn()) {
                    final FluxSink<ResultRow> fluxSink = sink.fluxSink;
                    if (fluxSink == null) {
                        nonExpectedType = ResultType.QUERY;
                        break;
                    } else {
                        final Consumer<ResultState> stateConsumer = sink.stateConsumer;
                        assert stateConsumer != null;
                        if (fluxSinkComplete(fluxSink, stateConsumer, state)) {
                            break;
                        }
                    }
                } else {
                    final MonoSink<ResultState> monoSink = sink.updateSink;
                    if (monoSink == null) {
                        nonExpectedType = ResultType.UPDATE;
                        break;
                    } else {
                        monoSink.success(state);
                    }
                }
            } else {
                throw AbstractResultSubscriber.createUnknownTypeError(currentResult);
            }

        }

        if (nonExpectedType != null) {
            addMultiResultSubscribeError(nonExpectedType);
        }

    }

    private void addMultiResultSubscribeError(final ResultType nonExpectedType) {
        final List<Throwable> errorList = this.errorList;
        if (errorList != null) {
            for (Throwable e : errorList) {
                if (e instanceof SubscribeException) {
                    return;
                }
            }
        }
        switch (nonExpectedType) {
            case QUERY: {
                addError(new SubscribeException(ResultType.UPDATE, nonExpectedType));
            }
            break;
            case UPDATE: {
                addError(new SubscribeException(ResultType.QUERY, nonExpectedType));
            }
            break;
            default:
                throw new IllegalArgumentException(String.format("nonExpectedType[%s]", nonExpectedType));
        }

    }


    private void subscribeInEventLoop(SinkWrapper sink) {
        if (hasError()) {
            this.sinkQueue.add(sink);
            drainError();
        } else if (this.done && this.resultQueue.isEmpty()) {
            sink.error(new NoMoreResultException("No more result."));
        } else {
            this.sinkQueue.add(sink);
            if (this.subscription == null) {
                // first subscribe
                this.source.subscribe(this);
            } else {
                drainResult();
            }

        }
    }


    private static final class ReactorMultiResultImpl implements MultiResult {

        private final ITaskAdjutant adjutant;

        private final MultiResultSubscriber subscriber;

        private ReactorMultiResultImpl(ITaskAdjutant adjutant, Publisher<Result> source) {
            this.adjutant = adjutant;
            this.subscriber = new MultiResultSubscriber(source);
        }

        @Override
        public final Mono<ResultState> nextUpdate() {
            return Mono.create(sink -> {
                if (this.adjutant.inEventLoop()) {
                    this.subscriber.subscribeInEventLoop(new SinkWrapper(sink));
                } else {
                    this.adjutant.execute(() -> this.subscriber.subscribeInEventLoop(new SinkWrapper(sink)));
                }
            });
        }

        @Override
        public final Flux<ResultRow> nextQuery(final Consumer<ResultState> statesConsumer) {
            return Flux.create(sink -> {
                if (this.adjutant.inEventLoop()) {
                    this.subscriber.subscribeInEventLoop(new SinkWrapper(sink, statesConsumer));
                } else {
                    this.adjutant.execute(() ->
                            this.subscriber.subscribeInEventLoop(new SinkWrapper(sink, statesConsumer)));
                }
            });
        }

        @Override
        public final Flux<ResultRow> nextQuery() {
            return nextQuery(JdbdFunctions.noActionConsumer());
        }


    }


    private static final class SinkWrapper {

        private final FluxSink<ResultRow> fluxSink;

        private final Consumer<ResultState> stateConsumer;

        private final MonoSink<ResultState> updateSink;

        private SinkWrapper(FluxSink<ResultRow> fluxSink, Consumer<ResultState> stateConsumer) {
            this.fluxSink = fluxSink;
            this.stateConsumer = stateConsumer;
            this.updateSink = null;
        }

        private SinkWrapper(MonoSink<ResultState> updateSink) {
            this.updateSink = updateSink;
            this.fluxSink = null;
            this.stateConsumer = null;
        }

        void error(JdbdException error) {
            if (this.fluxSink == null) {
                Objects.requireNonNull(this.updateSink)
                        .error(error);
            } else {
                this.fluxSink.error(error);
            }
        }


    }


}
