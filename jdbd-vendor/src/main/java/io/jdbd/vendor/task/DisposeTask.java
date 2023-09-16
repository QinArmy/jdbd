package io.jdbd.vendor.task;

/**
 * <p>
 * This interface representing dispose task, this interface means that when the sub-class of {@link CommunicationTask}
 * end , {@link CommunicationTaskExecutor}'s {@link reactor.netty.Connection} will be disposed.
 * <br/>
 * <p>
 * This interface is implemented by the sub-class of {@link CommunicationTask}.
 * <br/>
 *
 * @since 1.0
 */
public interface DisposeTask {


}
