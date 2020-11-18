package com.hab;

import com.hab.handlers.Handler2_StartTLS;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private Channel ch;

    public void run() throws Exception {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            final ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new WebSocketServerInitializer());

            ch = b.bind("localhost", 30001).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(30001);

        serverSocket.setEnabledCipherSuites(new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"});
    }

    private class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(final SocketChannel ch) throws Exception {
            final SSLEngine engine = SSL.createEngine(true, "localhost");
            engine.setUseClientMode(false);

            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("logger", new LoggingHandler(LogLevel.DEBUG));
            pipeline.addLast("ssl", new SslHandler(engine));
            pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
            pipeline.addLast("aggregator", new HttpObjectAggregator(1));
            pipeline.addLast("http-response-encoder", new HttpResponseEncoder());
            pipeline.addLast("request-handler", new WebSocketServerProtocolHandler("/websocket"));
            pipeline.addLast("Handler1_StartTLS", new Handler2_StartTLS());
        }
    }

}
