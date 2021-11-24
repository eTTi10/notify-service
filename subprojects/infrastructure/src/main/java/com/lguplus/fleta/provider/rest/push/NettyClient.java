package com.lguplus.fleta.provider.rest.push;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class NettyClient {

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel = null;

    private int connectTimeout = 3000; // 1 min

    public NettyClient() {
        init();
    }

    public void init() {
        /*
        group = new OioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(OioSocketChannel.class)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                //.option(ChannelOption.TCP_NODELAY, true)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_TIMEOUT, connectTimeout)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //socketChannel.config().setConnectTimeoutMillis(connectTimeout);
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new EchoClientHandler());
                    }
                });
        */
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                //.option(ChannelOption.TCP_NODELAY, true)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_TIMEOUT, connectTimeout)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //socketChannel.config().setConnectTimeoutMillis(connectTimeout);
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new EchoClientHandler());
                    }
                });
    }

    public Channel connect(final String host, final int port)  {
        try {
            //channel = bootstrap.connect(host, port).sync().channel();
            //ChannelFuture cf = channel.closeFuture();
            //channel.closeFuture().addListener(new ChannelCloseListener(this));
            // Start the client.
            ChannelFuture cf = bootstrap.connect(host, port).sync();
            cf.awaitUninterruptibly();
            // Wait till the session is finished initializing.
            this.channel = cf.channel();
            return getChannel();
        } catch (InterruptedException e) {
            close();
        }

        return null;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void waitForClose() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.warn("FileClient interrupted", e);
        }
    }

    public void sendRequest(String file) {
        //assert(file == null);
        //assert(channel == null);
        channel.write(file + "\r\n");
    }

    public void close() {
        if(channel != null) {
            channel.close();
            channel = null;
        }
        if ( group!=null) {
            group.shutdownGracefully();
        }
    }

    @Slf4j
    public static class EchoClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String sendMessage = "Hello, Netty!";

            ByteBuf messageBuffer = Unpooled.buffer();
            messageBuffer.writeBytes(sendMessage.getBytes());

            ctx.writeAndFlush(messageBuffer);

            log.debug("send message \"{}\"", sendMessage);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (log.isDebugEnabled()) {
                log.debug("receive message \"{}\"", ((ByteBuf) msg).toString(Charset.defaultCharset()));
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("exceptionCaught", cause);
            ctx.close();
        }
    }
}
