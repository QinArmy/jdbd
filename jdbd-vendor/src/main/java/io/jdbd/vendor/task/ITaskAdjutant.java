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
