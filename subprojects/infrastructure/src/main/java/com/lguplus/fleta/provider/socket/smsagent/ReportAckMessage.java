package com.lguplus.fleta.provider.socket.smsagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportAckMessage extends Message {

    public static final int TYPE = 5;

    private int result;

    @Override
    public int getType() {

        return TYPE;
    }

    @Override
    public int getLength() {

        return 4;
    }

    @Override
    protected byte[] marshal0() {

        final byte[] buffer = new byte[getLength()];
        MessageUtils.writeInt(result, buffer, 0);
        return buffer;
    }

    @Override
    public void unmarshal(byte[] buffer) {

        result = MessageUtils.readInt(buffer, 0);
    }
}
