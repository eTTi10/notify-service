package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith({MockitoExtension.class})
class MessageUnmarshallerTest {

    @Test
    void testGetRequiredBufferSize() {
        Message[] messages = new Message[] {
                new BindMessage(), new BindAckMessage(), new DeliverMessage(), new DeliverAckMessage(),
                new LinkSendMessage(), new LinkReceiveMessage(), new ReportMessage(), new ReportAckMessage()
        };
        Arrays.stream(messages).forEach(e -> {
            int length = MessageUnmarshaller.getRequiredBufferSize(e.getType());
            assertThat(length).isEqualTo(e.getLength());
        });
    }

    @Test
    void testUnmarshal() {
        Message[] messages = new Message[] {
                new BindMessage(), new BindAckMessage(), new DeliverMessage(), new DeliverAckMessage(),
                new LinkSendMessage(), new LinkReceiveMessage(), new ReportMessage(), new ReportAckMessage()
        };
        Arrays.stream(messages).forEach(e ->
                assertDoesNotThrow(() -> MessageUnmarshaller.unmarshal(e.getType(), new byte[e.getLength()]))
        );
    }

    @Test
    void testUnmarshalInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> MessageUnmarshaller.unmarshal(-1, null));
    }

    @Test
    void testUnmarshalException() {
        AtomicBoolean invalid = new AtomicBoolean();
        Message message = new Message() {
            {
                if (invalid.get()) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public int getType() {
                return 0;
            }
        };
        @SuppressWarnings("unchecked")
        Map<Integer, Message> availableMessages = (Map<Integer, Message>)ReflectionTestUtils.getField(MessageUnmarshaller.class, "AVAILABLE_MESSAGES");
        availableMessages.put(message.getType(), message);

        invalid.set(true);
        assertThrows(IllegalStateException.class, () -> MessageUnmarshaller.unmarshal(0, null));
    }
}
