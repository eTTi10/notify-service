package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith({MockitoExtension.class})
class MessageTest {

    @Test
    void testBindMessage() {
        byte[] buffer = BindMessage.builder()
                .id("test")
                .password("test")
                .build()
                .marshal();
        Message message = new BindMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testBindAckMessage() {
        byte[] buffer = BindAckMessage.builder()
                .prefix("019")
                .build()
                .marshal();
        Message message = new BindAckMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testDeliverMessage() {
        byte[] buffer = DeliverMessage.builder()
                .originAddress("01012345678")
                .destinationAddress("01023456789")
                .callback("01012345678")
                .text("테스트 메시지")
                .build()
                .marshal();
        Message message = new DeliverMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testDeliverAckMessage() {
        byte[] buffer = DeliverAckMessage.builder()
                .originAddress("01012345678")
                .destinationAddress("01023456789")
                .build()
                .marshal();
        Message message = new DeliverAckMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testReportMessage() {
        byte[] buffer = ReportMessage.builder()
                .originAddress("01012345678")
                .destinationAddress("01023456789")
                .deliveryTime("2020/01/01T00:00:00")
                .destinationCode("01990f")
                .build()
                .marshal();
        Message message = new ReportMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testReportAckMessage() {
        byte[] buffer = ReportAckMessage.builder()
                .build()
                .marshal();
        Message message = new ReportAckMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testLinkSendMessage() {
        byte[] buffer = LinkSendMessage.builder()
                .build()
                .marshal();
        Message message = new LinkSendMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }

    @Test
    void testLinkReceiveMessage() {
        byte[] buffer = LinkReceiveMessage.builder()
                .build()
                .marshal();
        Message message = new LinkReceiveMessage();
        message.unmarshal(Arrays.copyOfRange(buffer, 8, buffer.length));

        byte[] result = message.marshal();
        assertThat(result).isEqualTo(buffer);
    }
}
