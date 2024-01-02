/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.task;


import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class JdbdTimeoutTask implements TimeoutTask, Runnable {


    private static final AtomicReferenceFieldUpdater<JdbdTimeoutTask, TaskStatus> STATUS =
            AtomicReferenceFieldUpdater.newUpdater(JdbdTimeoutTask.class, TaskStatus.class, "status");

    private static final AtomicIntegerFieldUpdater<JdbdTimeoutTask> SUSPEND_STATUS =
            AtomicIntegerFieldUpdater.newUpdater(JdbdTimeoutTask.class, "suspendStatus");

    private static final AtomicLongFieldUpdater<JdbdTimeoutTask> RUN_TIME =
            AtomicLongFieldUpdater.newUpdater(JdbdTimeoutTask.class, "runTime");

    private final long startTime;
    private volatile TaskStatus status = TaskStatus.NONE;

    private volatile int suspendStatus = 0;

    private volatile long runTime;

    protected JdbdTimeoutTask() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public final void run() {
        if (this.status == TaskStatus.RUNNING) {
            return;
        }
        RUN_TIME.set(this, System.currentTimeMillis());
        if (STATUS.updateAndGet(this, this::updateToRunning) == TaskStatus.RUNNING && this.suspendStatus == 0) {
            runKillQuery();
        }

    }

    private void runKillQuery() {
        killQuery().doOnSuccess(v -> STATUS.compareAndSet(this, TaskStatus.RUNNING, TaskStatus.NORMAL_END))
                .subscribe();
    }


    @Override
    public final boolean isTimeout() {
        final boolean timeout;
        switch (STATUS.get(this)) {
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
    public final long runTimeMills() {
        return this.runTime;
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
        switch (this.status) {
            case NORMAL_END:
            case CANCELED_AND_END:
            case RUNNING:
                timeout = true;
                break;
            case CANCELED:
                timeout = false;
                break;
            case NONE:
            default:
                SUSPEND_STATUS.compareAndSet(this, 0, 1);
                timeout = false;

        }
        return timeout;
    }

    @Override
    public final boolean resume() {
        final boolean timeout;
        switch (this.status) {
            case NORMAL_END:
            case CANCELED_AND_END:
                timeout = true;
                break;
            case RUNNING: {
                timeout = true;
                if (SUSPEND_STATUS.compareAndSet(this, 1, 0)) {
                    runKillQuery();
                }
            }
            break;
            case NONE:
            case CANCELED:
            default:
                timeout = false;
                SUSPEND_STATUS.compareAndSet(this, 1, 0);
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
            case NONE:
            case RUNNING:
                newStatus = TaskStatus.RUNNING;
                break;
            case NORMAL_END:
            default:
                newStatus = oldStatus;

        }
        return newStatus;
    }

    private TaskStatus updateToCanceled(final TaskStatus oldStatus) {
        final TaskStatus newStatus;
        switch (oldStatus) {
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


}
