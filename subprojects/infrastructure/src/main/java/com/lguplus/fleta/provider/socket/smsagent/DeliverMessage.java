package com.lguplus.fleta.provider.socket.smsagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverMessage extends Message {

    public static final int TYPE = 2;

    private int tid;
    private String originAddress;
    private String destinationAddress;
    private String callback;
    private String text;
    private int serialNumber;

    @Override
    public int getType() {

        return TYPE;
    }

    @Override
    public int getLength() {

        return 264;
    }

    @Override
    protected byte[] marshal0() {

        final byte[] buffer = new byte[getLength()];
        MessageUtils.writeInt(tid, buffer, 0);
        MessageUtils.writeString(originAddress, buffer, 4, 32);
        MessageUtils.writeString(destinationAddress, buffer, 36, 32);
        MessageUtils.writeString(callback, buffer, 68, 32);
        MessageUtils.writeString(text, buffer, 100, 160);
        MessageUtils.writeInt(serialNumber, buffer, 260);
        return buffer;
    }

    @Override
    public void unmarshal(final byte[] buffer) {

        tid = MessageUtils.readInt(buffer, 0);
        originAddress = MessageUtils.readString(buffer, 4, 32);
        destinationAddress = MessageUtils.readString(buffer, 36, 32);
        callback = MessageUtils.readString(buffer, 68, 32);
        text = MessageUtils.readString(buffer, 100, 160);
        serialNumber = MessageUtils.readInt(buffer, 260);
    }
}
