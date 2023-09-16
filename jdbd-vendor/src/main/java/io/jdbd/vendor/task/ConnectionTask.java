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
