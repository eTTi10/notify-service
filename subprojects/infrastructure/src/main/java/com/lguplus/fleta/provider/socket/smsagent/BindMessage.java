package com.lguplus.fleta.provider.socket.smsagent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindMessage extends Message {

    public static final int TYPE = 0;

    private String id;
    private String password;

    @Override
    public int getType() {

        return TYPE;
    }

    @Override
    public int getLength() {

        return 32;
    }

    @Override
    protected byte[] marshal0() {

        final byte[] buffer = new byte[getLength()];
        MessageUtils.writeString(id, buffer, 0, 16);
        MessageUtils.writeString(password, buffer, 16, 16);
        return buffer;
    }

    @Override
    public void unmarshal(final byte[] buffer) {

        id = MessageUtils.readString(buffer, 0, 16);
        password = MessageUtils.readString(buffer, 16, 16);
    }
}
