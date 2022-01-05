package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.FailException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PushSocketInfoTest {

    String channelId = "01234567891234";//14char
    String PUSH_ENCODING = "euc-kr";

    @BeforeEach
    void setUp() {
    }

    //@Test
    void requestPushServer() {

    }

    @Test
    void isInValid() throws IOException {

        String channelId = "01234567890001";//14char
        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket("211.115.75.227", 9600, 2000, channelId, "222.231.13.85");
        Object normalSocket = ReflectionTestUtils.getField(pushSocketInfo, "pushSocket");
        assertTrue(!pushSocketInfo.isInValid());

        JunitTestUtils.setValue(pushSocketInfo, "pushSocket", null);
        assertTrue(pushSocketInfo.isInValid());
        JunitTestUtils.setValue(pushSocketInfo, "pushSocket", normalSocket);

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

        JunitTestUtils.setValue(pushSocketInfo, "pushSocket", socket);
        assertTrue(pushSocketInfo.isInValid());
        socket.close();
    }

    @Test
    void recvPushMessageBody() throws IOException {

        PushSocketInfo pushSocketInfo = new PushSocketInfo();
        pushSocketInfo.openSocket("211.115.75.227", 9600, 2000, channelId, "222.231.13.85");
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
        Assertions.assertEquals(responseDto.getStatusCode(), "200");
        Assertions.assertEquals(responseDto.getResponseCode(), "SC");

        testVo = PushSocketInfo.PushRcvHeaderVo.builder().status("FA").recvLength(byteBody.length).recvBuffer(byteBody).build();

        assertThrows(FailException.class, () -> {
            PushSocketInfo.PushRcvHeaderVo testVo1 = PushSocketInfo.PushRcvHeaderVo.builder().status("FA").recvLength(byteBody.length).recvBuffer(byteBody).build();
            ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo1);
        });

        assertThrows(FailException.class, () -> {
            PushSocketInfo.PushRcvHeaderVo testVo1 = PushSocketInfo.PushRcvHeaderVo.builder().status("--").recvLength(byteBody.length).recvBuffer(byteBody).build();
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

        PushSocketInfo.PushRcvHeaderVo testVo1 = PushSocketInfo.PushRcvHeaderVo.builder().status("SC").recvLength(byteBody1.length).recvBuffer(byteBody1).build();
        PushResponseDto responseDto1 = ReflectionTestUtils.invokeMethod(pushSocketInfo, "recvPushMessageBody", testVo1);
        Assertions.assertEquals(responseDto1.getStatusCode(), null);
        Assertions.assertEquals(responseDto1.getStatusMsg(), null);

        pushSocketInfo.closeSocket();
    }
}