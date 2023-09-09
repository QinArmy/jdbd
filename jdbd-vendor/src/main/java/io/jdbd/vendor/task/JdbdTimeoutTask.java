package io.jdbd.vendor.task;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class JdbdTimeoutTask implements TimeoutTask, Runnable {


    private static final AtomicReferenceFieldUpdater<JdbdTimeoutTask, TaskStatus> STATUS =
            AtomicReferenceFieldUpdater.newUpdater(JdbdTimeoutTask.class, TaskStatus.class, "status");


    private volatile TaskStatus status = TaskStatus.NONE;


    private final long startTime;

    protected JdbdTimeoutTask() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public final void run() {
        if (this.status == TaskStatus.RUNNING) {
            return;
        }
        if (STATUS.updateAndGet(this, this::updateToRunning) != TaskStatus.RUNNING) {
            return;
        }
        killQuery()
                .doOnSuccess(v -> STATUS.compareAndSet(this, TaskStatus.RUNNING, TaskStatus.NORMAL_END))
                .subscribe();
    }


    @Override
    public final boolean isTimeout() {
        final boolean timeout;
        switch (this.status) {
            case RUNNING:
            case CANCELED_AND_END:
            case NORMAL_END:
                timeout = true;
                break;
            default:
                timeout = false;
        }
        return timeout;
    }

    @Override
    public final long startTimeMills() {
        return this.startTime;
    }

    @Override
    public final TaskStatus currentStatus() {
        return STATUS.get(this);
    }

    @Override
    public final void cancel() {
        STATUS.updateAndGet(this, this::updateToCanceled);
    }

    @Override
    public final boolean suspend() {
        final boolean timeout;
        switch (STATUS.updateAndGet(this, this::updateToSuspend)) {
            case NORMAL_END:
            case CANCELED_AND_END:
            case RUNNING:
                timeout = true;
                break;
            case NONE:
            case CANCELED:
            case SUSPEND:
            default:
                timeout = false;

        }
        return timeout;
    }

    @Override
    public final boolean resume() {
        final boolean timeout;
        switch (STATUS.updateAndGet(this, this::updateToNone)) {
            case NORMAL_END:
            case RUNNING:
            case CANCELED_AND_END:
                timeout = true;
                break;
            case SUSPEND:
            case CANCELED:
            default:
                timeout = false;
        }
        return timeout;
    }

    @Override
    public final boolean isCanceled() {
        final boolean canceled;
        switch (STATUS.get(this)) {
            case CANCELED_AND_END:
            case CANCELED:
                canceled = true;
                break;
            default:
                canceled = false;
        }
        return canceled;
    }


    protected abstract Mono<Void> killQuery();


    private TaskStatus updateToRunning(final TaskStatus oldStatus) {
        final TaskStatus newStatus;
        switch (oldStatus) {
            case SUSPEND: // here , task end
                newStatus = TaskStatus.NORMAL_END;
                break;
            case NONE:
                newStatus = TaskStatus.RUNNING;
                break;
            default:
                newStatus = oldStatus;

        }
        return newStatus;
    }

    private TaskStatus updateToCanceled(final TaskStatus oldStatus) {
        final TaskStatus newStatus;
        switch (oldStatus) {
            case SUSPEND:
            case NONE:
                newStatus = TaskStatus.CANCELED;
                break;
            case RUNNING:
                newStatus = TaskStatus.CANCELED_AND_END;
                break;
            case NORMAL_END:
            case CANCELED_AND_END:
            case CANCELED:
            default:
                newStatus = oldStatus;

        }
        return newStatus;
    }

    private TaskStatus updateToSuspend(final TaskStatus oldStatus) {
        final TaskStatus newStatus;
        switch (oldStatus) {
            case SUSPEND:
            case NONE:
                newStatus = TaskStatus.SUSPEND;
                break;
            default:
                newStatus = oldStatus;

        }
        return newStatus;
    }

    private TaskStatus updateToNone(final TaskStatus oldStatus) {
        final TaskStatus newStatus;
        switch (oldStatus) {
            case SUSPEND:
                newStatus = TaskStatus.NONE;
                break;
            case CANCELED:
            case RUNNING:
            case NONE:
            case NORMAL_END:
            case CANCELED_AND_END:
            default:
                newStatus = oldStatus;

        }
        return newStatus;
    }


}
