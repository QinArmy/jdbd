package io.jdbd.vendor.task;

import io.jdbd.session.SessionCloseException;
import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface ITaskAdjutant {

    boolean isActive();

    boolean inEventLoop();

    /**
     * <p>
     * this method is used by {@link CommunicationTask} invoke for submit task to task queue of {@link CommunicationTaskExecutor}.
     * <br/>
     *
     * @throws IllegalStateException    throw when current thread not in {@link io.netty.channel.EventLoop}
     * @throws IllegalArgumentException throw when {@link CommunicationTask#getTaskPhase()} non-null
     * @throws SessionCloseException    throw then network channel closed
     * @see CommunicationTask#submit(Consumer)
     */
    void syncSubmitTask(CommunicationTask task, Runnable successCallBack);

    void execute(Runnable runnable);

    void schedule(Runnable command, long delay, TimeUnit unit);

    Mono<Void> logicallyClose();

    ByteBufAllocator allocator();

    /**
     * @return a unmodifiable set.
     */
    Set<EncryptMode> encryptModes();


}
