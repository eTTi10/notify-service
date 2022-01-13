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
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.provider.socket.PushMultiSocketClientImpl;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NettyTcpServerTest {
    static NettyTcpServer server;
    static Thread thread;
    static int testCnt = 9997;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9666;
    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;
    List<String> users = new ArrayList<>();
    static String responseCode = "200";
    static int responseCount = 0;
    static String responseTestMode = "normal";
    static String responseProcessFlag = "1";

    PushMultiSocketClientImpl pushMultiSocketClient;
    NettyTcpClient nettyTcpClient;

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
        nettyTcpClient = getNettyClient();
        pushMultiSocketClient = new PushMultiSocketClientImpl(nettyTcpClient);
        ReflectionTestUtils.setField(pushMultiSocketClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushMultiSocketClient, "maxLimitPush", "5");
        ReflectionTestUtils.setField(pushMultiSocketClient, "FLUSH_COUNT", 2);
        ReflectionTestUtils.setField(pushMultiSocketClient, "tranactionMsgId", new AtomicInteger((int)(Math.pow(16, 4)) -3));

        for(int i=0; i<10; i++) {
            users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        }

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

        NettyTcpServerTest.responseCode = "200";
        NettyTcpServerTest.responseCount = 0;
        NettyTcpServerTest.responseTestMode = "normal";
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

    @Test
    //normal case
    void testServer01 () {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

    }

    @Test
    //timeMillis >= SECOND
    void testServer02 () {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();

        ReflectionTestUtils.setField(pushMultiSocketClient, "lastSendMills", new AtomicLong(System.currentTimeMillis()-5000));
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

    }

    @Test //exception check
    void testServer03 () {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto))
                .users(pushRequestMultiDto.getUsers().stream().limit(1).collect(toList())).build();

        int [] errCode = new int[] {202, 400, 401, 403, 404};
        Class [] exlist = new Class[] {
                  AcceptedException.class
                , BadRequestException.class
                , UnAuthorizedException.class
                , ForbiddenException.class
                , NotFoundException.class   };

        for (int i =0; i<errCode.length; i++) {
            NettyTcpServerTest.responseCode = "" + errCode[i];

            assertThrows(exlist[i], () -> {
                pushMultiSocketClient.requestPushMulti(dto);
            });
        }

    }

    @Test //exception check : 410, 412, other
    void testServer04 () {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto))
                .users(pushRequestMultiDto.getUsers().stream().limit(2).collect(toList())).build();

        NettyTcpServerTest.responseCode = "410";
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

        NettyTcpServerTest.responseCode = "412";
        responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

        NettyTcpServerTest.responseCode = "499";
        responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("1130", responseMultiDto.getStatusCode());

        //NettyTcpServerTest.responseTestMode
    }

    @Test // test mode abnormal
    void testServer05 () {

        NettyTcpServerTest.responseTestMode = "abnormal";

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();

        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        log.debug("testServer05: {}", responseMultiDto);
        Assertions.assertEquals("1130", responseMultiDto.getStatusCode());
    }

    @Test // isServerInValidStatus
    void testServer06_isServerInValidStatus () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //Connect
        Method methodCheck = pushMultiSocketClient.getClass().getDeclaredMethod("checkGateWayServer");
        methodCheck.setAccessible(true);
        methodCheck.invoke(pushMultiSocketClient);

        //Server Status
        Method method = pushMultiSocketClient.getClass().getDeclaredMethod("isServerInValidStatus");

        NettyTcpServerTest.responseProcessFlag = "1";
        method.setAccessible(true);
        boolean status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertFalse(status);

        NettyTcpServerTest.responseProcessFlag = "0";
        status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertTrue(status);

        NettyTcpServerTest.responseProcessFlag = ""; // not return
        status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertTrue(status);

        NettyTcpServerTest.responseProcessFlag = "1";
    }

    @Test
    void testServer07_checkGateWayServer () {

        Assertions.assertTrue(true);
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
    final int SLEEP_MILLS = 10;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, PushMessageInfoDto message) throws InterruptedException {

        if (message.getMessageId() == PROCESS_STATE_REQUEST) {
            // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
            log.debug(":: MessageHandlerTest channelRead : PROCESS_STATE_REQUEST");

            Thread.sleep(SLEEP_MILLS);

            if(NettyTcpServerTest.responseProcessFlag.length() > 0 ) {
                ctx.writeAndFlush(PushMessageInfoDto.builder()
                        .messageId(PROCESS_STATE_REQUEST + 1)
                        .channelId(message.getChannelId())
                        .transactionId(message.getTransactionId())
                        .destinationIp(message.getDestinationIp())
                        .data("@Short!^" + NettyTcpServerTest.responseProcessFlag) //Success 1 , Fail  0
                        .build()
                );
            }
        }
        else if (message.getMessageId() == CHANNEL_CONNECTION_REQUEST) {
            // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
            log.debug(":: MessageHandlerTest channelRead : CHANNEL_CONNECTION_REQUEST");

            Thread.sleep(SLEEP_MILLS);

            ctx.writeAndFlush(PushMessageInfoDto.builder()
                    .messageId(CHANNEL_CONNECTION_REQUEST+1)
                    .channelId(message.getChannelId())
                    .transactionId(message.getTransactionId())
                    .destinationIp(message.getDestinationIp())
                    .data("SC")
                    .build()
            );
        }
        else if ("normal".equals(NettyTcpServerTest.responseTestMode) && message.getMessageId() == COMMAND_REQUEST) {
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
                            .replace("@StatusCode", NettyTcpServerTest.responseCode);
            PushMessageInfoDto dto =  PushMessageInfoDto.builder()
                    .messageId(COMMAND_REQUEST+1)
                    .channelId(message.getChannelId())
                    .transactionId(message.getTransactionId())
                    .destinationIp(message.getDestinationIp())
                    .data(sendData)
                    .build();
            log.debug(":: MessageHandlerTest channelWrite : COMMAND_REQUEST_ACK {}", dto);
            ctx.writeAndFlush(dto);
        }
        else if ("abnormal".equals(NettyTcpServerTest.responseTestMode) && message.getMessageId() == COMMAND_REQUEST) {
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
                    .replace("@StatusCode", NettyTcpServerTest.responseCode);
            //pushMultiClient.receiveAsyncMessage(PushMultiClient.MsgType.RECIVED_MSG, message);

            NettyTcpServerTest.responseCount++;

            int modeValue = 4;

            //normal
            if(NettyTcpServerTest.responseCount%modeValue == 1) {
                ctx.writeAndFlush(PushMessageInfoDto.builder()
                        .messageId(COMMAND_REQUEST + 1)
                        .channelId(message.getChannelId())
                        .transactionId(message.getTransactionId())
                        .destinationIp(message.getDestinationIp())
                        .data(sendData)
                        .build()
                );
            }
            else if(NettyTcpServerTest.responseCount%modeValue == 2) {
                // not response
            }
            else if(NettyTcpServerTest.responseCount%modeValue == 3) {
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
            }
            else { //normal
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

        //log.trace("[MessageHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageId() + ", " +
        //		message.getTransactionId() + ", " + message.getResult())
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error("[MessageHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());
    }
}

