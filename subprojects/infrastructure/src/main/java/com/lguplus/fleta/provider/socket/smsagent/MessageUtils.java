package com.lguplus.fleta.provider.socket.smsagent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class MessageUtils {

    private MessageUtils() {

        // Do nothing.
    }

    private static void checkBufferSize(final byte[] buffer, final int offset, final int required) {

        if (buffer.length - offset < required) {
            throw new IllegalArgumentException("Insufficient buffer size.");
        }
    }

    public static int readInt(final byte[] buffer, final int offset) {

        checkBufferSize(buffer, offset, 4);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, offset, 4);
        return byteBuffer.getInt();
    }

    public static void writeInt(final int value, final byte[] buffer, final int offset) {

        checkBufferSize(buffer, offset,4);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(value);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(byteBuffer.array(), 0, buffer, offset, 4);
    }

    public static String readString(final byte[] buffer, final int offset, final int length) {

        checkBufferSize(buffer, offset, length);

        return new String(Arrays.copyOfRange(buffer, offset, offset + length), Charset.forName("KSC5601")).trim();
    }

    public static void writeString(final String value, final byte[] buffer, final int offset, final int length) {

        checkBufferSize(buffer, offset, length);

        final byte[] bytes = value.getBytes(Charset.forName("KSC5601"));
        System.arraycopy(bytes, 0, buffer, offset, Math.min(bytes.length, length));
    }
}
