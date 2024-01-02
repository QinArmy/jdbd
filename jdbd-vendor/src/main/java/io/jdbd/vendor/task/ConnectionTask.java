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


import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;

public interface ConnectionTask {

    /**
     * <p>
     * this method invoke before {@link CommunicationTask#startTask(TaskSignal)}.
     * <br/>
     *
     * @param sslConsumer function ,implementation can add ssl by this function.
     */
    void addSsl(Consumer<SslWrapper> sslConsumer);

    /**
     * <p>
     * This will invoke :
     *     <ul>
     *         <li>after {@link CommunicationTask#decodeMessage(ByteBuf, Consumer)},if return value is {@code true}</li>
     *         <li>after send packet failure</li>
     *     </ul>
     *<br/>
     *
     * @return true:connect database server failure, {@link TaskExecutor} will close channel.
     */
    boolean disconnect();


}
