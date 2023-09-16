package io.jdbd.vendor.task;


/**
 * @see JdbdTimeoutTask
 * @since 1.0
 */
public interface TimeoutTask {

    boolean isTimeout();


    long startTimeMills();

    long runTimeMills();

    /**
     * cancel timeout task
     */
    void cancel();

    /**
     * <p>
     * suspend timeout task
     * <br/>
     *
     * @return true :  timeout
     */
    boolean suspend();


    /**
     * <p>
     * resume timeout task
     *<br/>
     *
     * @return true :  timeout
     */
    boolean resume();


    boolean isCanceled();

    TaskStatus currentStatus();


    enum TaskStatus {

        NONE,

        RUNNING,

        CANCELED,

        NORMAL_END,
        CANCELED_AND_END

    }

}
