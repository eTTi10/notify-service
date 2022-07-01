package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.FailException;
import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PushSocketInfoTest {

    static NettyTcpJunitServer server;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9600;

    String channelId = "0123456789123";//14char
    String PUSH_ENCODING = "euc-kr";

    int testTimeout = 2000;
    String testDestIp = "222.231.13.85";

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        server = new NettyTcpJunitServer();
        new Thread(() -> {
            server.runServer(SERVER_PORT);
        }).start();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_01_isInValid() throws IOException {

        String channelId = "01234567890001";//14char
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        try {
            pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "0", testDestIp);
        } catch (IOException ex) {

        }
        Object normalSocket = ReflectionTestUtils.getField(pushSocketInfo, "socket");
        assertTrue(!pushSocketInfo.isInValid());

        ReflectionTestUtils.setField(pushSocketInfo, "socket", normalSocket);

        ReflectionTestUtils.setField(pushSocketInfo, "isOpened", false);
        assertTrue(pushSocketInfo.isInValid());
        ReflectionTestUtils.setField(pushSocketInfo, "isOpened", true);

        ReflectionTestUtils.setField(pushSocketInfo, "isFailure", true);
        assertTrue(pushSocketInfo.isInValid());
        ReflectionTestUtils.setField(pushSocketInfo, "isFailure", false);

        pushSocketInfo.closeSocket();
    }

    @Test
    void test_02_isInValid2() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        Socket socket = new Socket();
        assertTrue(!socket.isConnected());

        ReflectionTestUtils.setField(pushSocketInfo, "socket", socket);
        assertTrue(pushSocketInfo.isInValid());
        socket.close();
    }

    @Test
    void test_03_recvPushMessageBody() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "1", testDestIp);
        //Object normalSocket = ReflectionTestUtils.getField(pushSocketInfo, "pushSocket");
        assertTrue(!pushSocketInfo.isInValid());

        //String returnJson = ""

        //byte[] bJsonMsg = new byte[pushRcvHeaderVo.getRecvLength() - 2];
        String responseJson = "SC{\n" +
            "\"response\" : {\n" +
            "\"msg_id\" : \"PUSH_NOTI\",\n" +
            "\"push_id\" : \"202201030820\",\n" +
            "\"status_code\" : \"200\",\n" +
            "\"statusmsg\" : \"@test_message\"\n" +
            "}\n" +
            "}";
        responseJson = responseJson.replaceAll("(\r\n|\n)", "");
        byte[] byteBody = responseJson.getBytes(PUSH_ENCODING);

        PushSocketInfo.PushRcvHeaderVo testVo = PushSocketInfo.PushRcvHeaderVo.builder().status("SC").recvLength(byteBody.length).recvBuffer(byteBody).build();
        PushResponseDto responseDto = ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo);
        assertEquals("200", responseDto.getStatusCode());
        assertEquals("SC", responseDto.getResponseCode());

        final PushSocketInfo.PushRcvHeaderVo testVo2 = PushSocketInfo.PushRcvHeaderVo.builder().status("FA").recvLength(byteBody.length).recvBuffer(byteBody).build();
        assertThrows(FailException.class, () -> {
            ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo2);
        });

        final PushSocketInfo.PushRcvHeaderVo testVo1 = PushSocketInfo.PushRcvHeaderVo.builder().status("--").recvLength(byteBody.length).recvBuffer(byteBody).build();
        assertThrows(FailException.class, () -> {
            ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo1);
        });

        String responseJson1 = "SC{\n" +
            "\"response\" : {\n" +
            "\"msg_id\" : \"PUSH_NOTI\",\n" +
            "\"push_id\" : \"202201030820\"\n" +
            "}\n" +
            "}";
        responseJson1 = responseJson1.replaceAll("(\r\n|\n)", "");
        byte[] byteBody1 = responseJson1.getBytes(PUSH_ENCODING);

        final PushSocketInfo.PushRcvHeaderVo testVo3 = PushSocketInfo.PushRcvHeaderVo.builder().status("SC").recvLength(byteBody1.length).recvBuffer(byteBody1).build();
        PushResponseDto responseDto1 = ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo3);
        assertEquals(null, responseDto1.getStatusCode());
        assertEquals(null, responseDto1.getStatusMsg());

        pushSocketInfo.closeSocket();
    }

    @Test
    void test_04_isServerInValidStatus() throws IOException {
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "2", testDestIp);

        PushSocketInfo spyPushSocket = spy(pushSocketInfo);
        short retSuccess = 1;//Success
        short retFailure = 0;//Success
        byte[] byteSuccess = new byte[2];
        //big endian
        byteSuccess[0] = (byte) (retSuccess >> 8);
        byteSuccess[1] = (byte) retSuccess;

        doReturn(retFailure).when(spyPushSocket).byteToShort(byteSuccess);

        long firstTime = spyPushSocket.getLastTransactionTime();

        ReflectionTestUtils.invokeMethod(spyPushSocket, "isServerInValidStatus");

        long lastTime = spyPushSocket.getLastTransactionTime();

        assertEquals(firstTime, lastTime);

        spyPushSocket.closeSocket();
    }

    @Test
    void test_05_connectPushServer2() throws IOException {
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        byte[] reqData = new byte[]{(byte) 0x53, (byte) 0x43, (byte) 0x00, (byte) 0x00};//SC

        doReturn("FA").when(spyPushSocket).getEncodeStr(reqData, 0, 2);
        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "3", testDestIp);

        assertTrue(!spyPushSocket.isOpened());
        spyPushSocket.closeSocket();

    }

    @Test
    void test_06_connectPushServer3() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "4", testDestIp);

        final int CHANNEL_PROCESS_STATE_REQUEST = 13;
        doThrow(new IOException("")).when(spyPushSocket).sendHeaderMsg(CHANNEL_PROCESS_STATE_REQUEST);//, null, null);

        long firstTime = spyPushSocket.getLastTransactionTime();
        //assertThrows(IOException.class, () -> {
        spyPushSocket.isServerInValidStatus();
        //ReflectionTestUtils.invokeMethod(spyPushSocket, "isServerInValidStatus");
        //});
        long lastTime = spyPushSocket.getLastTransactionTime();
        assertEquals(firstTime, lastTime);

        spyPushSocket.closeSocket();

    }

    @Test
    void test_07_closeSocket() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "5", testDestIp);

        doThrow(new IOException("")).when(spyPushSocket).closeSocketResource();
        assertThrows(IOException.class, () -> {
            spyPushSocket.closeSocketResource();
        });

        spyPushSocket.closeSocket();

    }

    @Test
    void test_08_readByteBuffer() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId + "6", testDestIp);

        int testValue = 999;
        ReflectionTestUtils.setField(pushSocketInfo, "mInputStream"
            , new BufferedInputStream(new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(testValue).putInt(testValue).array())));
        byte[] recvBytes = ReflectionTestUtils.invokeMethod(pushSocketInfo, "readByteBuffer", 4);
        ByteBuffer recvBuff = ByteBuffer.wrap(recvBytes, 0, 4);
        assertEquals(testValue, recvBuff.getInt());

        recvBytes = ReflectionTestUtils.invokeMethod(pushSocketInfo, "readByteBuffer", 8);
        recvBuff = ByteBuffer.wrap(recvBytes, 0, 4);

        //length == -1
        ReflectionTestUtils.setField(pushSocketInfo, "mInputStream"
            , new BufferedInputStream(new ByteArrayInputStream(ByteBuffer.allocate(0).array())));
        recvBytes = ReflectionTestUtils.invokeMethod(pushSocketInfo, "readByteBuffer", 4);

        pushSocketInfo.closeSocket();
    }
}