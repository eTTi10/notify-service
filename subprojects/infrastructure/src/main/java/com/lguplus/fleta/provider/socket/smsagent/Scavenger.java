package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Scavenger extends Thread {

    private static final int EXPIRE_TIME = 60000;
    private static final int SCAVENGE_INTERVAL = 300000;

    private final Map<?, DeliveryInfo> deliveryInfoMap;
    private boolean terminated;
    private Thread currentThread;

    public Scavenger(final Map<?, DeliveryInfo> deliveryInfoMap) {

        super();

        this.deliveryInfoMap = deliveryInfoMap;
    }

    @Override
    public void run() {

        currentThread = Thread.currentThread();
        while (!terminated) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(SCAVENGE_INTERVAL));

            final long currentTime = System.currentTimeMillis();
            deliveryInfoMap.entrySet().stream()
                    .filter(e -> e.getValue().getRequestTime() + EXPIRE_TIME < currentTime)
                    .forEach(e -> {
                        final Object key = e.getKey();
                        deliveryInfoMap.remove(key);

                        log.debug("Scavenge old deliver message {}", key);
                    });
        }
    }

    public void shutdown() {

        terminated = true;
        currentThread.interrupt();
    }
}
