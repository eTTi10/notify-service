package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SmsGateway {

    private static final SmsGatewayResponseDto SUCCESS_RESPONSE = SmsGatewayResponseDto.builder()
            .flag("0000")
            .message("성공")
            .build();
    private static final SmsGatewayResponseDto ERROR_RESPONSE = SmsGatewayResponseDto.builder()
            .flag("1500")
            .message("시스템 장애")
            .build();

    private final ConnectionManager connectionManager;
    @Getter
    private long lastSendTime;

    public SmsGateway(final String ip, final int port, final String id, final String password) {

        connectionManager = new ConnectionManager(ip, port, id, password);
        connectionManager.start();
    }

    public SmsGatewayResponseDto deliver(final String sender, final String receiver, final String message)
            throws IOException {

        lastSendTime = System.currentTimeMillis();
        final DeliverAckMessage result = connectionManager.sendDeliverMessage(sender, receiver, message);
        if (result != null && result.getResult() == 0) {
            return SUCCESS_RESPONSE;
        }
        return ERROR_RESPONSE;
    }

    public boolean isBounded() {

        return connectionManager.isBounded();
    }
}
