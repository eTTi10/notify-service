package com.lguplus.fleta.provider.socket.smsagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverAckMessage extends Message {

    public static final int TYPE = 3;

    private int result;
    private String originAddress;
    private String destinationAddress;
    private int serialNumber;

    @Override
    public int getType() {

        return TYPE;
    }

    @Override
    public int getLength() {

        return 72;
    }

    @Override
    protected byte[] marshal0() {

        final byte[] buffer = new byte[getLength()];
        MessageUtils.writeInt(result, buffer, 0);
        MessageUtils.writeString(originAddress, buffer, 4, 32);
        MessageUtils.writeString(destinationAddress, buffer, 36, 32);
        MessageUtils.writeInt(serialNumber, buffer, 68);
        return buffer;
    }

    @Override
    public void unmarshal(final byte[] buffer) {

        result = MessageUtils.readInt(buffer, 0);
        originAddress = MessageUtils.readString(buffer, 4, 32);
        destinationAddress = MessageUtils.readString(buffer, 36, 32);
        serialNumber = MessageUtils.readInt(buffer, 68);
    }
}
