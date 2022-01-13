package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.FailException;
import com.lguplus.fleta.provider.socket.multi.NettyTcpServer;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PushSocketInfoTest {

    static NettyTcpServer server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9666;

    String channelId = "0123456789123";//14char
    String PUSH_ENCODING = "euc-kr";

    int testTimeout = 2000;
    String testDestIp = "222.231.13.85";

    @BeforeEach
    void setUp() {
    }

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
    }

    @Test
    void isInValid() throws IOException {

        String channelId = "01234567890001";//14char
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"0", testDestIp);
        Object normalSocket = ReflectionTestUtils.getField(pushSocketInfo, "socket");
        assertTrue(!pushSocketInfo.isInValid());

        JunitTestUtils.setValue(pushSocketInfo, "socket", normalSocket);

        JunitTestUtils.setValue(pushSocketInfo, "isOpened", false);
        assertTrue(pushSocketInfo.isInValid());
        JunitTestUtils.setValue(pushSocketInfo, "isOpened", true);

        JunitTestUtils.setValue(pushSocketInfo, "isFailure", true);
        assertTrue(pushSocketInfo.isInValid());
        JunitTestUtils.setValue(pushSocketInfo, "isFailure", false);

        pushSocketInfo.closeSocket();
    }

    @Test
    void isInValid2() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        Socket socket = new Socket();
        assertTrue(!socket.isConnected());

        JunitTestUtils.setValue(pushSocketInfo, "socket", socket);
        assertTrue(pushSocketInfo.isInValid());
        socket.close();
    }

    @Test
    void recvPushMessageBody() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"1", testDestIp);
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
        Assertions.assertEquals("200", responseDto.getStatusCode());
        Assertions.assertEquals("SC", responseDto.getResponseCode());

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
        Assertions.assertEquals(null, responseDto1.getStatusCode());
        Assertions.assertEquals(null, responseDto1.getStatusMsg());

        pushSocketInfo.closeSocket();
    }

    @Test
    void test_isServerInValidStatus() throws IOException {
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"2", testDestIp);

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

        Assertions.assertEquals(firstTime, lastTime);

        spyPushSocket.closeSocket();
    }

    @Test
    void test_connectPushServer2() throws IOException {
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        byte[] reqData = new byte[]{(byte) 0x53, (byte) 0x43, (byte) 0x00, (byte) 0x00};//SC

        doReturn("FA").when(spyPushSocket).getEncodeStr(reqData, 0, 2);
        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"3", testDestIp);

        assertTrue(!spyPushSocket.isOpened());
        spyPushSocket.closeSocket();

    }

    @Test
    void test_connectPushServer3() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"4", testDestIp);

        final int CHANNEL_PROCESS_STATE_REQUEST = 13;
        doThrow(new IOException("")).when(spyPushSocket).sendHeaderMsg(CHANNEL_PROCESS_STATE_REQUEST);//, null, null);

        long firstTime = spyPushSocket.getLastTransactionTime();
        //assertThrows(IOException.class, () -> {
        spyPushSocket.isServerInValidStatus();
            //ReflectionTestUtils.invokeMethod(spyPushSocket, "isServerInValidStatus");
        //});
        long lastTime = spyPushSocket.getLastTransactionTime();
        Assertions.assertEquals(firstTime, lastTime);

        spyPushSocket.closeSocket();

    }

    @Test
    void test_closeSocket() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        PushSocketInfo spyPushSocket = spy(pushSocketInfo);

        spyPushSocket.openSocket(SERVER_IP, SERVER_PORT, testTimeout, channelId+"5", testDestIp);

        doThrow(new IOException("")).when(spyPushSocket).closeSocketResource();
        assertThrows(IOException.class, () -> {
            spyPushSocket.closeSocketResource();
        });

        spyPushSocket.closeSocket();

    }
}