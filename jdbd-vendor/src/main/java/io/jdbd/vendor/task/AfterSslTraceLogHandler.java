package io.jdbd.vendor.task;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.NettyPipeline;

final class AfterSslTraceLogHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AfterSslTraceLogHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf && LOG.isTraceEnabled()) {
            ByteBuf byteBuf = (ByteBuf) msg;
            LOG.trace("receive server packet before {}, readableBytes = {}"
                    , NettyPipeline.ReactiveBridge, byteBuf.readableBytes());
        }
        ctx.fireChannelRead(msg);
    }

}
