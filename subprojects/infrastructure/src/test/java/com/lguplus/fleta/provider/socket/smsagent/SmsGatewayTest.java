package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
class SmsGatewayTest {

    @Test
    void testIsBounded() {

        try (MockedConstruction<ConnectionManager> ignored = mockConstruction(ConnectionManager.class, (mock, context) ->
                doReturn(true).when(mock).isBounded()
        )) {
            SmsGateway smsGateway = spy(new SmsGateway("localhost", 0, "test", "test"));
            boolean result = smsGateway.isBounded();
            assertTrue(result);
        }
    }

    @Test
    void testDeliver() throws IOException {

        try (MockedConstruction<ConnectionManager> ignored = mockConstruction(ConnectionManager.class, (mock, context) ->
                doReturn(DeliverAckMessage.builder().result(0).build()).when(mock).sendDeliverMessage(any(), any(), any())
        )) {
            SmsGateway smsGateway = spy(new SmsGateway("localhost", 0, "test", "test"));
            SmsGatewayResponseDto result = smsGateway.deliver("01012345678", "01023456789", "테스트");
            assertThat(result.getFlag()).isEqualTo("0000");
        }
    }

    @Test
    void testDeliverReturnsError() throws IOException {

        try (MockedConstruction<ConnectionManager> ignored = mockConstruction(ConnectionManager.class, (mock, context) ->
                doReturn(DeliverAckMessage.builder().result(1).build()).when(mock).sendDeliverMessage(any(), any(), any())
        )) {
            SmsGateway smsGateway = spy(new SmsGateway("localhost", 0, "test", "test"));
            SmsGatewayResponseDto result = smsGateway.deliver("01012345678", "01023456789", "테스트");
            assertThat(result.getFlag()).isEqualTo("1500");
        }
    }

    @Test
    void testDeliverReturnsNull() throws IOException {

        try (MockedConstruction<ConnectionManager> ignored = mockConstruction(ConnectionManager.class, (mock, context) ->
                doReturn(null).when(mock).sendDeliverMessage(any(), any(), any())
        )) {
            SmsGateway smsGateway = spy(new SmsGateway("localhost", 0, "test", "test"));
            SmsGatewayResponseDto result = smsGateway.deliver("01012345678", "01023456789", "테스트");
            assertThat(result.getFlag()).isEqualTo("1500");
        }
    }
}