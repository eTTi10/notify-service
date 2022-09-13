package com.lguplus.fleta.provider.socket.smsagent;

public abstract class Message {

    private static final int LENGTH_WITHOUT_BODY = 0;

    public abstract int getType();

    public int getLength() {

        return LENGTH_WITHOUT_BODY;
    }

    public final byte[] marshal() {

        final byte[] buffer = new byte[8 + getLength()];
        MessageUtils.writeInt(getType(), buffer, 0);
        MessageUtils.writeInt(getLength(), buffer, 4);
        System.arraycopy(marshal0(), 0, buffer, 8, getLength());
        return buffer;
    }

    protected byte[] marshal0() {

        return new byte[0];
    }

    public void unmarshal(byte[] buffer) {

        // Do nothing.
    }
}
