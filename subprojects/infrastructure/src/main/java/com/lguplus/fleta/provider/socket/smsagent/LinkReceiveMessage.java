package com.lguplus.fleta.provider.socket.smsagent;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class LinkReceiveMessage extends Message {

    public static final int TYPE = 7;

    @Override
    public int getType() {

        return TYPE;
    }
}
