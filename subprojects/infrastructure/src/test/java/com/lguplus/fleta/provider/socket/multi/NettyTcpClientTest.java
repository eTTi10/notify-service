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
import fleta.util.JunitTestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NettyTcpClientTest implements PushMultiClient {

    //NettyTcpClient nettyTcpClient = new NettyTcpClient();
    //String channelID;

    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;
    List<String> users = new ArrayList<>();

    AtomicInteger tranactionMsgId = new AtomicInteger(0);
    static int testCnt = 9997;

    //@Mock
    NettyTcpClient.MessageHandler messageHandler = new NettyTcpClient.MessageHandler();

    //@Mock
    NettyTcpClient.MessageDecoder messageDecoder = new NettyTcpClient.MessageDecoder();

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

    private String getMessage(PushRequestMultiDto dto) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PushMultiClient.PUSH_COMMAND);
        paramMap.put("push_id", PushMultiClient.TRANSACT_ID_NM);
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
        paramMap.put("service_passwd", getSha512Pwd(dto.getServiceId()));

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

    private String getSha512Pwd(String servicePwd) {
        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
    }

    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto) {
        return null;
    }

    PushMessageInfoDto receivedMessage = null;
    MsgType receivedMsgType;
    CountDownLatch receivedLatch;// = new CountDownLatch(1);
    @Override
    public void receiveAsyncMessage(MsgType msgType, PushMessageInfoDto dto) {
        if(msgType == MsgType.SEND_SUCCESS_MSG || msgType == MsgType.SEND_FAIL_MSG) {
            receivedMessage = dto;
            receivedMsgType = msgType;
            receivedLatch.countDown();
        }
        else if(msgType == MsgType.RECIVED_MSG) {
            log.debug("** receiveAsyncMessage {} , {}", msgType, dto);
        }
    }

    private String getTransactionId() {
        String DATE_FOMAT = "yyyyMMdd";
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04x", tranactionMsgId.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0) & 0xFFFF);
    }

    @Test
    void test_requestPushMulti() throws InterruptedException {
        //nettyTcpClient = new NettyTcpClient();
        NettyTcpClient nettyTcpClient = new NettyTcpClient();

        ReflectionTestUtils.setField(nettyTcpClient, "host", "211.115.75.227");
        ReflectionTestUtils.setField(nettyTcpClient, "port", "9600");
        ReflectionTestUtils.setField(nettyTcpClient, "timeout", "2000");
        ReflectionTestUtils.setField(nettyTcpClient, "wasPort", "8080");
        ReflectionTestUtils.setField(nettyTcpClient, "defaultSocketChannelId", "PsAGT");
        ReflectionTestUtils.setField(nettyTcpClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(nettyTcpClient, "callRetryCount", "2");

        ReflectionTestUtils.setField(nettyTcpClient, "commChannelNum", new AtomicInteger(++testCnt));

        String channelID = nettyTcpClient.connect(this);
        String transactionId = getTransactionId();
        int COMMAND_REQUEST = 15;

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                .replace(REGIST_ID_NM, pushRequestMultiDto.getUsers().get(0));

        receivedLatch = new CountDownLatch(1);

        PushMessageInfoDto sendDto = PushMessageInfoDto.builder().messageId(COMMAND_REQUEST)
                .channelId(channelID).transactionId(transactionId)
                .destinationIp("222.231.13.85").data(jsonMsg).build();

        log.debug("nettyTcpClient.isInValid {} {} {}", nettyTcpClient.isInValid(), channelID, sendDto);
        Assertions.assertFalse(nettyTcpClient.isInValid());
        nettyTcpClient.write(sendDto);
        nettyTcpClient.flush();

        receivedLatch.await(2000, TimeUnit.MILLISECONDS);

        Assertions.assertEquals(MsgType.SEND_SUCCESS_MSG, receivedMsgType);
        Assertions.assertEquals(transactionId, receivedMessage.getTransactionId());

        Thread.sleep(3000);
        nettyTcpClient.disconnect();
    }

    @Test
    void test_requestPushMulti2() throws InterruptedException {
        NettyTcpClient nettyTcpClient = new NettyTcpClient();

        ReflectionTestUtils.setField(nettyTcpClient, "host", "211.115.75.227");
        ReflectionTestUtils.setField(nettyTcpClient, "port", "9600");
        ReflectionTestUtils.setField(nettyTcpClient, "timeout", "2000");
        ReflectionTestUtils.setField(nettyTcpClient, "wasPort", "8080");
        ReflectionTestUtils.setField(nettyTcpClient, "defaultSocketChannelId", "PsAGT");
        ReflectionTestUtils.setField(nettyTcpClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(nettyTcpClient, "callRetryCount", "2");

        ReflectionTestUtils.setField(nettyTcpClient, "commChannelNum", new AtomicInteger(++testCnt));

        String channelID = nettyTcpClient.connect(this);

        String transactionId = getTransactionId();
        int PROCESS_STATE_REQUEST = 13;

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                .replace(REGIST_ID_NM, pushRequestMultiDto.getUsers().get(0));

        PushMessageInfoDto response = (PushMessageInfoDto) nettyTcpClient.writeSync(
                PushMessageInfoDto.builder().messageId(PROCESS_STATE_REQUEST)
                        .channelId(channelID).destinationIp("222.231.13.85").build());

        int processSatusId = response.getMessageId();
        Assertions.assertEquals(14, processSatusId);

        Thread.sleep(3000);
        nettyTcpClient.disconnect();

    }

    @Test
    void test_messageHandler() {

        Test01Client test01 = new Test01Client();
        JunitTestUtils.setValue(messageHandler, "pushMultiClient", test01);

        int COMMAND_REQUEST_ACK = 16;
        int UNKNOWN = 200;

        messageHandler.channelRead(null, PushMessageInfoDto.builder().messageId(COMMAND_REQUEST_ACK).result("SC").build());
        Assertions.assertEquals(COMMAND_REQUEST_ACK, test01.pushMessageInfoDto.getMessageId());

        messageHandler.channelRead(null, PushMessageInfoDto.builder().messageId(UNKNOWN).result("SC").build());
        //Assertions.assertEquals(COMMAND_REQUEST_ACK, test01.pushMessageInfoDto.getMessageId());

        //exceptionCaught isActive
        ChannelTest channelActive = new ChannelTest("id", true, true);
        ChannelHandlerContext channelHandlerContextActive = new ChannelHandlerContextTest(channelActive);
        messageHandler.exceptionCaught(channelHandlerContextActive, new Throwable("@Exception"));

        //exceptionCaught isActive == false
        channelActive = new ChannelTest("id", true, false);
        channelHandlerContextActive = new ChannelHandlerContextTest(channelActive);
        messageHandler.exceptionCaught(channelHandlerContextActive, new Throwable("@Exception"));

       // nettyTcpClient.channel.
    }

    public static class Test01Client implements PushMultiClient {

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

     @Test
    void test_messageDecoder() throws Exception {
        ChannelTest channel = new ChannelTest("id", true, false);
        ChannelHandlerContext channelHandlerContext = new ChannelHandlerContextTest(channel);

         ChannelTest channelActive = new ChannelTest("id", true, true);
         ChannelHandlerContext channelHandlerContextActive = new ChannelHandlerContextTest(channelActive);

        List<Object> out;
        ByteBuf byteBuf;
        byte[] byteData;
        PushMessageInfoDto messageInfoDto;

        //isActive
        int PUSH_MSG_HEADER_LEN = 64;
        out = new ArrayList<>();
        byteBuf = Unpooled.buffer(PUSH_MSG_HEADER_LEN);
        messageDecoder.decode(channelHandlerContext, byteBuf, out);
        byteBuf.release();
        Assertions.assertEquals(0, out.size());

        // less than PUSH_MSG_HEADER_LEN
        out = new ArrayList<>();
        byteBuf = Unpooled.buffer(PUSH_MSG_HEADER_LEN-1);
        messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
        byteBuf.release();
        Assertions.assertEquals(0, out.size());

         // normal message
         out = new ArrayList<>();

         int CHANNEL_CONNECTION_REQUEST_ACK = 2;
         int PROCESS_STATE_REQUEST_ACK = 14;
         int COMMAND_REQUEST_ACK = 16;

         //CHANNEL_CONNECTION_REQUEST_ACK
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(CHANNEL_CONNECTION_REQUEST_ACK).channelId("0123456789ABC1")
                 .data("SC").destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         byteBuf = Unpooled.wrappedBuffer(byteData);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(1, out.size());

         //PROCESS_STATE_REQUEST_ACK SC
         out = new ArrayList<>();
         byte[] byteData1 = new byte[PUSH_MSG_HEADER_LEN + 2];
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(PROCESS_STATE_REQUEST_ACK).channelId("0123456789ABC1")
                 .data("").destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         System.arraycopy(Ints.toByteArray(2), 0, byteData, 60, 4);
         System.arraycopy(byteData, 0, byteData1, 0, byteData.length);
         System.arraycopy(Shorts.toByteArray((short) 1), 0, byteData1, 64, 2);
         byteBuf = Unpooled.wrappedBuffer(byteData1);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(1, out.size());

         //PROCESS_STATE_REQUEST_ACK FA
         out = new ArrayList<>();
         byte[] byteData2 = new byte[PUSH_MSG_HEADER_LEN + 2];
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(PROCESS_STATE_REQUEST_ACK).channelId("0123456789ABC1")
                 .data("").destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         System.arraycopy(Ints.toByteArray(2), 0, byteData, 60, 4);
         System.arraycopy(byteData, 0, byteData2, 0, byteData.length);
         System.arraycopy(Shorts.toByteArray((short) 0), 0, byteData2, 64, 2);
         byteBuf = Unpooled.wrappedBuffer(byteData2);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(1, out.size());

        //COMMAND_REQUEST_ACK
         out = new ArrayList<>();
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(COMMAND_REQUEST_ACK).channelId("0123456789ABC1")
                 .data("SC{\"response\" : {\"msg_id\" : \"PUSH_NOTI\",\"push_id\" : \"202201100001\",\"status_code\" : \"200\"}}").destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         byteBuf = Unpooled.wrappedBuffer(byteData);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(1, out.size());

         //isValidMessageType
         int invalidMessageId = 999;
         out = new ArrayList<>();
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(invalidMessageId).channelId("0123456789ABC1")
                 .data("SC").destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         byteBuf = Unpooled.wrappedBuffer(byteData);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(0, out.size());

         //in.readableBytes() < dataLength
         out = new ArrayList<>();
         String msg = "SC{\"response\" : {\"msg_id\" : \"PUSH_NOTI\",\"push_id\" : \"202201100001\",\"status_code\" : \"200\"}}";
         byte[] msgData = msg.getBytes( "euc-kr");
         messageInfoDto = PushMessageInfoDto.builder()
                 .messageId(COMMAND_REQUEST_ACK).channelId("0123456789ABC1")
                 .data(msg).destinationIp("222.231.13.85").transactionId(getTransactionId()).build();
         byteData = messageTobyteArr(messageInfoDto);
         System.arraycopy(Ints.toByteArray(msgData.length + 10), 0, byteData, 60, 4);
         byteBuf = Unpooled.wrappedBuffer(byteData);
         messageDecoder.decode(channelHandlerContextActive, byteBuf, out);
         byteBuf.release();
         Assertions.assertEquals(0, out.size());

    }

    private byte[] messageTobyteArr(PushMessageInfoDto message) throws Exception {

        int PUSH_MSG_HEADER_LEN = 64;
        String PUSH_ENCODING = "euc-kr";

        byte[] dataInfo = message.getData().getBytes(PUSH_ENCODING);

        byte[] byteTotalData = new byte[PUSH_MSG_HEADER_LEN + dataInfo.length];
        System.arraycopy(Ints.toByteArray(message.getMessageId()), 0, byteTotalData, 0, 4);                    //Message Id
        System.arraycopy(message.getTransactionId().getBytes(PUSH_ENCODING), 0, byteTotalData, 4, message.getTransactionId().getBytes(PUSH_ENCODING).length);//12);   //Transaction Id
        System.arraycopy(message.getChannelId().getBytes(PUSH_ENCODING), 0, byteTotalData, 16, message.getChannelId().getBytes(PUSH_ENCODING).length);//14);             //Channel Id
        System.arraycopy(message.getDestinationIp().getBytes(PUSH_ENCODING), 0, byteTotalData, 32, message.getDestinationIp().getBytes(PUSH_ENCODING).length);//Destination IP
        System.arraycopy(Ints.toByteArray(dataInfo.length), 0, byteTotalData, 60, 4);                 //Data Length
        //if(dataInfo.length > 0)
        System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);

        log.debug("messageTobyteArr {}", message);

        return byteTotalData;
    }

    public static class ChannelHandlerContextTest implements ChannelHandlerContext {

        ChannelTest channelTest;

        public ChannelHandlerContextTest(ChannelTest channel) {
            channelTest = channel;
        }

        @Override
        public Channel channel() {
            return channelTest;
        }

        @Override
        public EventExecutor executor() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public ChannelHandler handler() {
            return null;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelUnregistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelActive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelInactive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireUserEventTriggered(Object evt) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object msg) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelReadComplete() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelWritabilityChanged() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelHandlerContext read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelHandlerContext flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return false;
        }
    }

    public static class ChannelTest implements Channel {

        String id;
        boolean isOpen;
        boolean isActive;

        public ChannelTest(String id, boolean opened, boolean actived) {
            this.id = id;
            this.isActive = actived;
            this.isOpen = opened;
        }

        @Override
        public ChannelId id() {
            return new ChannelId() {
                @Override
                public String asShortText() {
                    return id;
                }

                @Override
                public String asLongText() {
                    return id;
                }

                @Override
                public int compareTo(ChannelId o) {
                    return 0;
                }
            };
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public EventLoop eventLoop() {
            return null;
        }

        @Override
        public Channel parent() {
            return null;
        }

        @Override
        public ChannelConfig config() {
            return null;
        }

        @Override
        public boolean isRegistered() {
            return false;
        }
        @Override
        public ChannelMetadata metadata() {
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            return null;
        }

        @Override
        public ChannelFuture closeFuture() {
            return null;
        }

        @Override
        public boolean isWritable() {
            return false;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return 0;
        }

        @Override
        public long bytesBeforeWritable() {
            return 0;
        }

        @Override
        public Unsafe unsafe() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return null;
        }

        @Override
        public Channel read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public Channel flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return false;
        }

        @Override
        public int compareTo(Channel o) {
            return 0;
        }
    }


    @AfterEach
    void tearDown() {

    }

}