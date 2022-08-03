package com.lguplus.fleta.provider.socket.smsagent;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;

public class DeliveryInfo {

    @Getter
    private final long requestTime;
    private final CountDownLatch timeoutLatch;
    @Getter
    private DeliverAckMessage result;

    public DeliveryInfo(final CountDownLatch timeoutLatch) {

        this.requestTime = System.currentTimeMillis();
        this.timeoutLatch = timeoutLatch;
    }

    public void onResultReceived(final DeliverAckMessage result) {

        this.result = result;
        this.timeoutLatch.countDown();
    }
}
