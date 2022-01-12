package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NettyTcpServerTest {
    static NettyTcpServer server;
    static Thread thread;
    static int testCnt = 9997;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9666;
    AtomicInteger tranactionMsgId = new AtomicInteger(0);
    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;
    List<String> users = new ArrayList<>();

    @BeforeAll
    static void setUpAll() {
        server = new NettyTcpServer();
        thread = new Thread(() -> {
            server.runServer(SERVER_PORT);
        });
        thread.start();
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
        //thread.interrupt();
    }

    @BeforeEach
    void setUp() {
        //boolean check = nettyTcpClient.isInValid();

        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");

        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestMultiDto = PushRequestMultiDto.builder()
                .serviceId("30011")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .users(users)
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();
    }

    @Test
    void testServer01 () {

        //Connect
        NettyTcpClient nettyTcpClient = getNettyClient();
        String channelID = nettyTcpClient.connect(new Test02Client());
        Assertions.assertFalse(nettyTcpClient.isInValid());

        String transactionId = getTransactionId();
        int PROCESS_STATE_REQUEST = 13;
        String REGIST_ID_NM = "[@RegistId]";
        String TRANSACT_ID_NM = "[@TransactionId]";

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                .replace(REGIST_ID_NM, pushRequestMultiDto.getUsers().get(0));

        PushMessageInfoDto response = (PushMessageInfoDto) nettyTcpClient.writeSync(
                            PushMessageInfoDto.builder().messageId(PROCESS_STATE_REQUEST)
                                .channelId(channelID).destinationIp("222.231.13.85").build());
        Assertions.assertEquals(14, response.getMessageId());


        //nettyTcpClient.disconnect();

    }
    private String getTransactionId() {
        String DATE_FOMAT = "yyyyMMdd";
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04x", tranactionMsgId.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0) & 0xFFFF);
    }

    private NettyTcpClient getNettyClient() {
        NettyTcpClient nettyTcpClient = new NettyTcpClient();

        ReflectionTestUtils.setField(nettyTcpClient, "host", SERVER_IP);
        ReflectionTestUtils.setField(nettyTcpClient, "port", "" + SERVER_PORT);
        ReflectionTestUtils.setField(nettyTcpClient, "timeout", "2000");
        ReflectionTestUtils.setField(nettyTcpClient, "wasPort", "8080");
        ReflectionTestUtils.setField(nettyTcpClient, "defaultSocketChannelId", "PsAGT");
        ReflectionTestUtils.setField(nettyTcpClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(nettyTcpClient, "callRetryCount", "2");

        ReflectionTestUtils.setField(nettyTcpClient, "commChannelNum", new AtomicInteger(++testCnt));

        return nettyTcpClient;
    }

    private String getMessage(PushRequestMultiDto dto) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PushMultiClient.PUSH_COMMAND);
        paramMap.put("push_id", PushMultiClient.TRANSACT_ID_NM);
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
        paramMap.put("service_passwd", dto.getServiceId());

        if (PushMultiClient.LG_PUSH_OLD.equals("00007")) {
            paramMap.put("push_app_id", "smartux0001");
            paramMap.put("noti_type", "POS");
            paramMap.put("regist_id", PushMultiClient.REGIST_ID_NM);
        } else {
            paramMap.put("service_key", PushMultiClient.REGIST_ID_NM);
        }

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode oNode = objectMapper.createObjectNode();
        oNode.set("request", objectMapper.valueToTree(paramMap));
        return oNode.toString();
    }

}

@Slf4j
class NettyTcpServer {
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public void runServer(int port) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializerTest());

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
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}

class Test02Client implements PushMultiClient {

    public PushMessageInfoDto pushMessageInfoDto;

    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto) {
        return null;
    }

    @Override
    public void receiveAsyncMessage(MsgType msgType, PushMessageInfoDto dto) {
        pushMessageInfoDto = dto;
    }
}

class ChannelInitializerTest extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 핸들러 설정
        pipeline.addLast("decoder", new MessageDecoderTest());
        pipeline.addLast("encoder", new MessageEncoderTest());
        pipeline.addLast("handler", new MessageHandlerTest());
    }
}


@Slf4j
@NoArgsConstructor
class MessageEncoderTest extends MessageToByteEncoder<PushMessageInfoDto> {
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

        log.debug("MessageEncoderTest #1 {}", message);

        byte[] dataInfo = message.getData().getBytes(PUSH_ENCODING);
        int dataLen = dataInfo.length;
        if(message.getData().startsWith("@Short!^")) {
            dataLen = 2;
        }

        log.debug("MessageEncoderTest #2 {}", message);

        byte[] byteTotalData = new byte[PUSH_MSG_HEADER_LEN + dataLen];
        System.arraycopy(Ints.toByteArray(message.getMessageId()), 0, byteTotalData, 0, 4);                    //Message Id
        System.arraycopy(message.getTransactionId().getBytes(PUSH_ENCODING), 0, byteTotalData, 4, message.getTransactionId().getBytes(PUSH_ENCODING).length);   //Transaction Id
        System.arraycopy(message.getDestinationIp().getBytes(PUSH_ENCODING), 0, byteTotalData, 16, message.getDestinationIp().getBytes(PUSH_ENCODING).length);  //Destination IP
        System.arraycopy(message.getChannelId().getBytes(PUSH_ENCODING), 0, byteTotalData, 32, message.getChannelId().getBytes(PUSH_ENCODING).length);          //Channel Id

        System.arraycopy(Ints.toByteArray(dataLen), 0, byteTotalData, 60, 4);                 //Data Length

        if(message.getData().startsWith("@Short!^")) {
            log.debug("MessageEncoderTest #2-1 @Short Length:{}", dataLen);
            byte[] short2bytes = Shorts.toByteArray((short)(message.getData().equals("@Short!^1") ? 1 : 0));
            log.debug("MessageEncoderTest #2-1 @Short data:{} total:{}", short2bytes, byteTotalData.length);
            System.arraycopy(short2bytes, 0, byteTotalData, 64, short2bytes.length);
        }
        else {
            log.debug("MessageEncoderTest #2-2 normal {}", message.getData());
            System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);
        }

        log.debug("MessageEncoderTest #3 {}", message);

        out.writeBytes(byteTotalData);
    }
}

@Slf4j
@NoArgsConstructor
class MessageDecoderTest extends ByteToMessageDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    final String PUSH_ENCODING = "euc-kr";
    final int PUSH_MSG_HEADER_LEN = 64;
    final String SUCCESS = "SC";
    final String FAIL = "FA";
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
                log.debug("** MessageDecoderTest decode message: CHANNEL_CONNECTION_REQUEST {}", CHANNEL_CONNECTION_REQUEST);

                //result = new String(byteData,0, 2, PUSH_ENCODING);
                pushMessageInfoDto = PushMessageInfoDto.builder()
                        .messageId(messageID)
                        .channelId(channelId)
                        .destinationIp(destIp)
                        .build();
                log.debug("** MessageDecoderTest CHANNEL_CONNECTION_REQUEST {}", pushMessageInfoDto);
                out.add(pushMessageInfoDto);
                break;

            case PROCESS_STATE_REQUEST:
                log.debug("** MessageDecoderTest decode message: PROCESS_STATE_REQUEST {}", PROCESS_STATE_REQUEST);

                //result = byteToShort(byteData) == 1 ? SUCCESS : FAIL;
                pushMessageInfoDto = PushMessageInfoDto.builder()
                        .messageId(messageID)
                        .channelId(channelId)
                        .destinationIp(destIp)
                        .build();
                log.debug("** MessageDecoderTest  PROCESS_STATE_REQUEST {}", pushMessageInfoDto);
                out.add(pushMessageInfoDto);
                break;

            case COMMAND_REQUEST:
                log.debug("** MessageDecoderTest decode message: COMMAND_REQUEST {}", COMMAND_REQUEST);

                //result = new String(byteData,0, 2, PUSH_ENCODING);
                String transactionID = new String(byteHeader, 4, 12, PUSH_ENCODING);
                String data = new String(byteData,0, byteData.length, PUSH_ENCODING);

                pushMessageInfoDto = PushMessageInfoDto.builder()
                        .messageId(messageID)
                        .transactionId(transactionID)
                        .channelId(channelId)
                        .destinationIp(destIp)
                        .data(data)
                        .build();

                log.debug("** MessageDecoderTest  COMMAND_REQUEST {}", pushMessageInfoDto);
                //NettyTcpClient.PushRcvStatusMsgWrapperVo msgWrapperVo = objectMapper.readValue(data, NettyTcpClient.PushRcvStatusMsgWrapperVo.class);
                //String statusCode = msgWrapperVo.getResponse().getStatusCode();
                out.add(pushMessageInfoDto);
                break;
            default:
                log.error("MessageDecoderTest unknown message {}", messageID);
                break;
        }

        log.debug("** MessageDecoderTest decode end: {}", out.size());
    }

    private int byteToInt(byte[] src, int offset) {
        return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
    }

}

@Slf4j
@NoArgsConstructor
class MessageHandlerTest extends SimpleChannelInboundHandler<PushMessageInfoDto> {

    final int CHANNEL_CONNECTION_REQUEST = 1;
    final int PROCESS_STATE_REQUEST = 13;
    final int COMMAND_REQUEST = 15;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, PushMessageInfoDto message) {

        if (message.getMessageId() == PROCESS_STATE_REQUEST) {
            // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
            log.debug(":: MessageHandlerTest channelRead : PROCESS_STATE_REQUEST");

            ctx.writeAndFlush(PushMessageInfoDto.builder()
                            .messageId(PROCESS_STATE_REQUEST+1)
                            .channelId(message.getChannelId())
                            .transactionId(message.getTransactionId())
                            .destinationIp(message.getDestinationIp())
                            .data("@Short!^1") //Success
                            .build()
            );
           // ctx.channel().flush();
        }
        else if (message.getMessageId() == CHANNEL_CONNECTION_REQUEST) {
            // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
            log.debug(":: MessageHandlerTest channelRead : CHANNEL_CONNECTION_REQUEST");

            ctx.writeAndFlush(PushMessageInfoDto.builder()
                    .messageId(CHANNEL_CONNECTION_REQUEST+1)
                    .channelId(message.getChannelId())
                    .transactionId(message.getTransactionId())
                    .destinationIp(message.getDestinationIp())
                    .data("SC")
                    .build()
            );
            //ctx.channel().flush();
        }
        else if (message.getMessageId() == COMMAND_REQUEST) {
            // Push 전송인 경우 response 결과를 임시 Map에 저장함.
            log.debug(":: MessageHandlerTest channelRead : COMMAND_REQUEST {}", message);

            String data = "SC{\n" +
                    "\"response\" : {\n" +
                    "\"msg_id\" : \"PUSH_NOTI\",\n" +
                    "\"push_id\" : \"@TransactionId\",\n" +
                    "\"status_code\" : \"@StatusCode\"\n" +
                    "}\n" +
                    "}";
            //pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.RECIVED_MSG, message);

            String sendData = data.replace("@TransactionId", message.getTransactionId())
                            .replace("@StatusCode", "200");
            ctx.writeAndFlush(PushMessageInfoDto.builder()
                    .messageId(COMMAND_REQUEST+1)
                    .channelId(message.getChannelId())
                    .transactionId(message.getTransactionId())
                    .destinationIp(message.getDestinationIp())
                    .data(sendData)
                    .build()
            );
           // ctx.channel().flush();
        }

        //log.trace("[MessageHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageId() + ", " +
        //		message.getTransactionId() + ", " + message.getResult())
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error("[MessageHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());
    }
}

