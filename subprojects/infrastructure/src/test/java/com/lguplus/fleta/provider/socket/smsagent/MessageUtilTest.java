package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith({MockitoExtension.class})
class MessageUtilTest {

    @Test
    void testWriteIntAndReadInt() {
        int value = 200;
        byte[] buffer = new byte[32];

        MessageUtils.writeInt(value, buffer, 10);
        int result = MessageUtils.readInt(buffer, 10);
        assertThat(result).isEqualTo(value);
    }

    @Test
    void testWriteStringAndReadString() {
        String value = "테스트";
        byte[] buffer = new byte[32];

        MessageUtils.writeString(value, buffer, 10, 20);
        String result = MessageUtils.readString(buffer, 10, 20);
        assertThat(result).isEqualTo(value);
    }

    @Test
    void testCheckBufferSizeInsufficientBufferSize() {
        int value = 200;
        byte[] buffer = new byte[32];

        MessageUtils.writeInt(value, buffer, 10);
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.readInt(buffer, 30));
    }
}
