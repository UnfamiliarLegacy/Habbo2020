package com.hab.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler1_Translator extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(Handler1_Translator.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("Converting BinaryWebSocketFrame > ByteBuf");

        if (msg instanceof BinaryWebSocketFrame) {
            ctx.fireChannelRead(((BinaryWebSocketFrame) msg).content());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        logger.info("Converting ByteBuf > BinaryWebSocketFrame");

        if (msg instanceof ByteBuf) {
            ctx.write(new BinaryWebSocketFrame((ByteBuf) msg), promise);
        } else {
            ctx.write(msg, promise);
        }
    }

}
