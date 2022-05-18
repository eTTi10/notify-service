package com.lguplus.fleta.provider.socket.smsagent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NettySmsAgentServer {

    public String responseCode = "200";
    public int responseCount = 0;
    public String responseTestMode = "normal";
    public String responseProcessFlag = "1";
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public void runServer(int port) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializerTest(this));

            // 포트 지정
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
            stopServer();
        }
    }

    public void stopServer() {
        log.debug("stop Server:");
        try {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    static class ChannelInitializerTest extends ChannelInitializer<SocketChannel> {
        NettySmsAgentServer nettyTcpServer;

        public ChannelInitializerTest(NettySmsAgentServer server) {
            this.nettyTcpServer = server;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            // 핸들러 설정
            pipeline.addLast("decoder", new MessageDecoderTest());
            pipeline.addLast("encoder", new MessageEncoderTest());
            pipeline.addLast("handler", new MessageHandlerTest());
        }
    }

    static class MessageEncoderTest extends MessageToByteEncoder<SmsMessage> {

        @Override
        protected void encode(ChannelHandlerContext ctx, SmsMessage message, ByteBuf out) throws Exception {

            out.writeInt(message.type)
                    .writeInt(message.body.length)
                    .writeBytes(message.body);
        }
    }

    static class MessageDecoderTest extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            Channel channel = ctx.channel();

            log.debug("MessageDecoder in.readableBytes:{}", in.readableBytes());

            if (!channel.isActive()) {
                log.debug(":: MessageDecoderTest : isActive Error");
                return;
            }

            in.markReaderIndex();
            if (in.readableBytes() < 8) {
                log.trace(":: MessageDecoderTest : less than message header");
                return;
            }

            int type = in.readInt();
            int length = in.readInt();

            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            byte[] body = new byte[length];
            in.readBytes(body);
            out.add(SmsMessage.builder()
                    .type(type)
                    .body(body)
                    .build());
        }
    }

    static class MessageHandlerTest extends SimpleChannelInboundHandler<SmsMessage> {

        @Override
        public void channelRead0(ChannelHandlerContext ctx, SmsMessage message) throws InterruptedException {

            // BIND
            if (message.type == 0) {
                ByteBuf buf = ctx.alloc().buffer();
                buf.writeInt(0);

                byte[] body = new byte[20];
                buf.readBytes(body, 0, 4);
                ctx.writeAndFlush(SmsMessage.builder()
                        .type(1)
                        .body(body)
                        .build());
            }
            // DELIVER
            else if (message.type == 2) {
                ByteBuf buf = ctx.alloc().buffer();
                buf.writeInt(0);

                byte[] body = new byte[72];
                buf.readBytes(body, 0, 4);
                ctx.writeAndFlush(SmsMessage.builder()
                        .type(3)
                        .body(body)
                        .build());
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
            log.error("[MessageHandlerTest] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());
        }
    }

    @Builder
    static class SmsMessage {
        int type;
        byte[] body;
    }
}
