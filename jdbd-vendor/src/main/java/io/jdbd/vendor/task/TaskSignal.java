package io.jdbd.vendor.task;


import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;

interface TaskSignal {


    void sendPacket(CommunicationTask task, Publisher<ByteBuf> packets, boolean endTask);


}
