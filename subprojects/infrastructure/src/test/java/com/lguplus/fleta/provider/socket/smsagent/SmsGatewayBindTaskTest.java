package com.lguplus.fleta.provider.socket.smsagent;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
//@ExtendWith({MockitoExtension.class})
//@TestMethodOrder(MethodOrderer.MethodName.class)
class SmsGatewayBindTaskTest {

//    @Mock
//    SmsGateway smsGateway;
//
//    SmsGateway.BindTimerTask bindTimerTask;
//
//    SmsGateway.LinkTimerTask linkTimerTask;
//
//    SmsGateway.ErrorTimerTask errorTimerTask;
//
//    @Test
//    @DisplayName("BindTimerTask 테스트")
//    void test_01() throws IOException {
//
//        doNothing().when(smsGateway).checkLink();
//
//        bindTimerTask = new SmsGateway.BindTimerTask(smsGateway);
//        assertDoesNotThrow(bindTimerTask::run);
//
//        doThrow(IOException.class).when(smsGateway).checkLink();
//        assertDoesNotThrow(bindTimerTask::run);
//    }
//
//    @Test
//    @DisplayName("LinkTimerTask 테스트")
//    void test_02() throws IOException {
//
//        ReflectionTestUtils.setField(smsGateway, "isLinked", true);
//        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
//        assertDoesNotThrow(linkTimerTask::run);
//
//        doNothing().when(smsGateway).connectGateway();
//        ReflectionTestUtils.setField(smsGateway, "isLinked", false);
//        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
//        assertDoesNotThrow(linkTimerTask::run);
//
//    }
//
//    @Test
//    @DisplayName("ErrorTimerTask 테스트")
//    void test_03() throws IOException {
//
//        ReflectionTestUtils.setField(smsGateway, "mResult", "");
//        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
//        assertDoesNotThrow(errorTimerTask::run);
//
//        ReflectionTestUtils.setField(smsGateway, "mResult", "0000");
//        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
//        assertDoesNotThrow(errorTimerTask::run);
//
//    }
}