package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Linker extends Thread {

    private static final int LINK_INTERVAL = 50000;
    private static final int READ_TIMEOUT = 5000;

    private final MessageSender messageSender;
    private final Connector connector;

    private boolean terminate;
    private CountDownLatch timeoutLatch;
    private Thread currentThread;

    public Linker(final MessageSender messageSender, final Connector connector) {

        super();

        this.messageSender = messageSender;
        this.connector = connector;
    }

    @Override
    public void run() {

        currentThread = Thread.currentThread();
        while (!terminate) {
            try {
                timeoutLatch = new CountDownLatch(1);
                messageSender.sendLinkSendMessage();
                if (timeoutLatch.await(READ_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(LINK_INTERVAL));
                } else {
                    throw new IllegalStateException("Failed to receive link receive message");
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                log.error(e.getMessage(), e);

                shutdown();
                connector.reconnect();
            }
        }
    }

    public void onLinkReceived() {

        if (timeoutLatch != null) {
            timeoutLatch.countDown();
        }
    }

    public void shutdown() {

        terminate = true;
        currentThread.interrupt();
    }
}
