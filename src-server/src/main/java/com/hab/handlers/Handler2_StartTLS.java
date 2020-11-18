package com.hab.handlers;

import com.hab.SSL;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;

public class Handler2_StartTLS extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(Handler2_StartTLS.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        logger.info("Received data in Handler1_StartTLS");

        ByteBuf content = frame.content();

        // Check if content is "StartTLS".
        if (content.readByte() == 0x53 && content.readByte() == 0x74 && content.readableBytes() == 6) {
            // Setup pipeline.
            final SSLEngine engine = SSL.createEngine(false, "localhost");
            engine.setUseClientMode(false);

            ctx.pipeline().remove(this);
            ctx.pipeline().addLast("Handler1_StartTLS", new Handler1_Translator());
            ctx.pipeline().addLast("ssl_starttls", new SslHandler(engine, false));

            // TODO: ctx.pipeline().addLast another handler that will process and send packets like normal.

            // Send reply.
            ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer("OK".getBytes())));
        }
    }
}
