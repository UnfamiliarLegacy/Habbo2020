package com.hab.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler3_Test extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(Handler3_Test.class);

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        logger.info("Handler2_Stuff - Received ByteBuf");

        ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("OK".getBytes()));
        // ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer("OK".getBytes())));
    }

}
