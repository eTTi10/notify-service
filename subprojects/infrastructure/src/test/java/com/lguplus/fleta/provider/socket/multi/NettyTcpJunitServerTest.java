package com.lguplus.fleta.provider.socket.multi;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NettyTcpJunitServerTest {
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
            future.channel().closeFuture().sync();
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
        NettyTcpJunitServerTest nettyTcpServer;

        public ChannelInitializerTest(NettyTcpJunitServerTest server) {
            this.nettyTcpServer = server;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            // 핸들러 설정
            pipeline.addLast("decoder", new MessageDecoderTest());
            pipeline.addLast("encoder", new MessageEncoderTest());
            pipeline.addLast("handler", new MessageHandlerTest(nettyTcpServer));
        }
    }

    static class MessageEncoderTest extends MessageToByteEncoder<PushMessageInfoDto> {
        final String PUSH_ENCODING = "euc-kr";
        final int PUSH_MSG_HEADER_LEN = 64;

        @Override
        protected void encode(ChannelHandlerContext ctx, PushMessageInfoDto message, ByteBuf out) throws Exception {
            /* server -> client header
             * Message Header Structure (64Byte)
             * ------------------------------------------------------------------------------
             *   Message ID(4)  |  Transaction ID(12)  |         Destination IP(16)
             * ------------------------------------------------------------------------------
             *      Channel ID(14)     | Reserved 1(2) |  Reserved 2(12)  |  Data Length(4)
             * ------------------------------------------------------------------------------
             */

            byte[] dataInfo = message.getData().getBytes(PUSH_ENCODING);
            int dataLen = dataInfo.length;
            if (message.getData().startsWith("@Short!^")) {
                dataLen = 2;
            }

            if(message.getMessageId() == 2) {//SC00
                dataLen = 4;
            }

            byte[] byteTotalData = new byte[PUSH_MSG_HEADER_LEN + dataLen];
            System.arraycopy(Ints.toByteArray(message.getMessageId()), 0, byteTotalData, 0, 4);                    //Message Id
            System.arraycopy(message.getTransactionId().getBytes(PUSH_ENCODING), 0, byteTotalData, 4, message.getTransactionId().getBytes(PUSH_ENCODING).length);   //Transaction Id
            System.arraycopy(message.getDestinationIp().getBytes(PUSH_ENCODING), 0, byteTotalData, 16, message.getDestinationIp().getBytes(PUSH_ENCODING).length);  //Destination IP
            System.arraycopy(message.getChannelId().getBytes(PUSH_ENCODING), 0, byteTotalData, 32, message.getChannelId().getBytes(PUSH_ENCODING).length);          //Channel Id

            System.arraycopy(Ints.toByteArray(dataLen), 0, byteTotalData, 60, 4);                 //Data Length

            if(message.getMessageId() == 2) {//connect ack SC+00, FA+(short code)
                System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);
                byte[] short2bytes = Shorts.toByteArray((short) (0));
                System.arraycopy(short2bytes, 0, byteTotalData, 64+2, 2);
            }
            else if (message.getData().startsWith("@Short!^")) {
                byte[] short2bytes = Shorts.toByteArray((short) (message.getData().equals("@Short!^1") ? 1 : 0));
                System.arraycopy(short2bytes, 0, byteTotalData, 64, short2bytes.length);
            } else {
                System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);
            }

            log.debug("MessageEncoderTest {}", message);

            out.writeBytes(byteTotalData);
        }
    }

    static class MessageDecoderTest extends ByteToMessageDecoder {

        final String PUSH_ENCODING = "euc-kr";
        final int PUSH_MSG_HEADER_LEN = 64;
        final int CHANNEL_CONNECTION_REQUEST = 1;
        final int PROCESS_STATE_REQUEST = 13;
        final int COMMAND_REQUEST = 15;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            /* client -> server header
             * Message Header Structure (64Byte)
             * ------------------------------------------------------------------------------
             *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
             * ------------------------------------------------------------------------------
             *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
             * ------------------------------------------------------------------------------
             */

            Channel channel = ctx.channel();

            log.debug("MessageDecoder in.readableBytes:{}", in.readableBytes());

            if (!channel.isActive()) {
                log.debug(":: MessageDecoderTest : isActive Error");
                return;
            }

            in.markReaderIndex();
            if (in.readableBytes() < PUSH_MSG_HEADER_LEN) {
                log.trace(":: MessageDecoderTest : less than PUSH_MSG_HEADER_LEN");
                return;
            }

            // Message header
            byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];
            in.readBytes(byteHeader);

            int messageID = byteToInt(byteHeader, 0);
            int dataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);

            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }

            // Message body
            byte[] byteData = new byte[dataLength];
            in.readBytes(byteData);

            String result;
            String channelId = new String(byteHeader, 16, 14, PUSH_ENCODING);
            String destIp = new String(byteHeader, 32, 16, PUSH_ENCODING);

            PushMessageInfoDto pushMessageInfoDto;

            switch (messageID) {
                case CHANNEL_CONNECTION_REQUEST:
                    pushMessageInfoDto = PushMessageInfoDto.builder()
                            .messageId(messageID)
                            .channelId(channelId)
                            .destinationIp(destIp)
                            .build();
                    log.debug("** MessageDecoderTest CHANNEL_CONNECTION_REQUEST {}", pushMessageInfoDto);
                    out.add(pushMessageInfoDto);
                    break;

                case PROCESS_STATE_REQUEST:
                    pushMessageInfoDto = PushMessageInfoDto.builder()
                            .messageId(messageID)
                            .channelId(channelId)
                            .destinationIp(destIp)
                            .build();
                    log.debug("** MessageDecoderTest  PROCESS_STATE_REQUEST {}", pushMessageInfoDto);
                    out.add(pushMessageInfoDto);
                    break;

                case COMMAND_REQUEST:
                    String transactionID = new String(byteHeader, 4, 12, PUSH_ENCODING);
                    String data = new String(byteData, 0, byteData.length, PUSH_ENCODING);

                    pushMessageInfoDto = PushMessageInfoDto.builder()
                            .messageId(messageID)
                            .transactionId(transactionID)
                            .channelId(channelId)
                            .destinationIp(destIp)
                            .data(data)
                            .build();

                    log.debug("** MessageDecoderTest  COMMAND_REQUEST {}", pushMessageInfoDto);

                    out.add(pushMessageInfoDto);
                    break;
            }
        }

        private int byteToInt(byte[] src, int offset) {
            return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
        }

    }

    static class MessageHandlerTest extends SimpleChannelInboundHandler<PushMessageInfoDto> {

        final int CHANNEL_CONNECTION_REQUEST = 1;
        final int PROCESS_STATE_REQUEST = 13;
        final int COMMAND_REQUEST = 15;
        final int SLEEP_MILLS = 10;

        NettyTcpJunitServerTest nettyTcpServer;

        public MessageHandlerTest(NettyTcpJunitServerTest server) {
            this.nettyTcpServer = server;
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, PushMessageInfoDto message) throws InterruptedException {

            if (message.getMessageId() == PROCESS_STATE_REQUEST) {
                // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
                log.debug(":: MessageHandlerTest channelRead : PROCESS_STATE_REQUEST");

                Thread.sleep(SLEEP_MILLS);

                if (nettyTcpServer.responseProcessFlag.length() > 0) {
                    ctx.writeAndFlush(PushMessageInfoDto.builder()
                            .messageId(PROCESS_STATE_REQUEST + 1)
                            .channelId(message.getChannelId())
                            .transactionId(message.getTransactionId())
                            .destinationIp(message.getDestinationIp())
                            .data("@Short!^" + nettyTcpServer.responseProcessFlag) //Success 1 , Fail  0
                            .build()
                    );
                }
            } else if (message.getMessageId() == CHANNEL_CONNECTION_REQUEST) {
                // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
                log.debug(":: MessageHandlerTest channelRead : CHANNEL_CONNECTION_REQUEST");

                Thread.sleep(SLEEP_MILLS);

                ctx.writeAndFlush(PushMessageInfoDto.builder()
                        .messageId(CHANNEL_CONNECTION_REQUEST + 1)
                        .channelId(message.getChannelId())
                        .transactionId(message.getTransactionId())
                        .destinationIp(message.getDestinationIp())
                        .data("SC")
                        .build()
                );
            } else if ("normal".equals(nettyTcpServer.responseTestMode) && message.getMessageId() == COMMAND_REQUEST) {
                // Push 전송인 경우 response 결과를 임시 Map에 저장함.
                log.debug(":: MessageHandlerTest channelRead : COMMAND_REQUEST normal {}", message);

                Thread.sleep(SLEEP_MILLS);

                String data = "SC{\n" +
                        "\"response\" : {\n" +
                        "\"msg_id\" : \"PUSH_NOTI\",\n" +
                        "\"push_id\" : \"@TransactionId\",\n" +
                        "\"status_code\" : \"@StatusCode\"\n" +
                        "}\n" +
                        "}";

                String sendData = data.replace("@TransactionId", message.getTransactionId())
                        .replace("@StatusCode", nettyTcpServer.responseCode);
                PushMessageInfoDto dto = PushMessageInfoDto.builder()
                        .messageId(COMMAND_REQUEST + 1)
                        .channelId(message.getChannelId())
                        .transactionId(message.getTransactionId())
                        .destinationIp(message.getDestinationIp())
                        .data(sendData)
                        .build();
                log.debug(":: MessageHandlerTest channelWrite : COMMAND_REQUEST_ACK {}", dto);
                ctx.writeAndFlush(dto);
            } else if ("abnormal".equals(nettyTcpServer.responseTestMode) && message.getMessageId() == COMMAND_REQUEST) {
                // Push 전송인 경우 response 결과를 임시 Map에 저장함.
                log.debug(":: MessageHandlerTest channelRead : COMMAND_REQUEST abnormal {}", message);

                Thread.sleep(SLEEP_MILLS);

                String data = "SC{\n" +
                        "\"response\" : {\n" +
                        "\"msg_id\" : \"PUSH_NOTI\",\n" +
                        "\"push_id\" : \"@TransactionId\",\n" +
                        "\"status_code\" : \"@StatusCode\"\n" +
                        "}\n" +
                        "}";
                String sendData = data.replace("@TransactionId", message.getTransactionId())
                        .replace("@StatusCode", nettyTcpServer.responseCode);
                //pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.RECIVED_MSG, message);

                nettyTcpServer.responseCount++;

                int modeValue = 4;

                //normal
                if (nettyTcpServer.responseCount % modeValue == 1) {
                    ctx.writeAndFlush(PushMessageInfoDto.builder()
                            .messageId(COMMAND_REQUEST + 1)
                            .channelId(message.getChannelId())
                            .transactionId(message.getTransactionId())
                            .destinationIp(message.getDestinationIp())
                            .data(sendData)
                            .build()
                    );
                } else if (nettyTcpServer.responseCount % modeValue == 2) {
                    // not response
                } else if (nettyTcpServer.responseCount % modeValue == 3) {
                    // delay time
                    Thread.sleep(500);
                    ctx.writeAndFlush(PushMessageInfoDto.builder()
                            .messageId(COMMAND_REQUEST + 1)
                            .channelId(message.getChannelId())
                            .transactionId(message.getTransactionId())
                            .destinationIp(message.getDestinationIp())
                            .data(sendData)
                            .build()
                    );
                } else { //normal
                    ctx.writeAndFlush(PushMessageInfoDto.builder()
                            .messageId(COMMAND_REQUEST + 1)
                            .channelId(message.getChannelId())
                            .transactionId(message.getTransactionId())
                            .destinationIp(message.getDestinationIp())
                            .data(sendData)
                            .build()
                    );
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
            log.error("[MessageHandlerTest] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());
        }
    }
}