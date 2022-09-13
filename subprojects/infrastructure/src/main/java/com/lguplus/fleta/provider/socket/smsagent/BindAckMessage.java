package com.lguplus.fleta.provider.socket.smsagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindAckMessage extends Message {

    public static final int TYPE = 1;

    private int result;
    private String prefix;

    @Override
    public int getType() {

        return TYPE;
    }

    @Override
    public int getLength() {

        return 20;
    }

    @Override
    protected byte[] marshal0() {

        final byte[] buffer = new byte[getLength()];
        MessageUtils.writeInt(result, buffer, 0);
        MessageUtils.writeString(prefix, buffer, 4, 16);
        return buffer;
    }

    @Override
    public void unmarshal(final byte[] buffer) {

        result = MessageUtils.readInt(buffer, 0);
        prefix = MessageUtils.readString(buffer, 4, 16);
    }
}
