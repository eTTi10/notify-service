package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ConnectionManager extends Thread implements Connector, MessageSender, MessageHandler {

    private static final int RECONNECT_INTERVAL = 180000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 60000;

    private final String ip;
    private final int port;
    private final String id;
    private final String password;

    private final Scavenger scavenger;

    private boolean connected;
    private boolean bounded;
    private boolean terminated;
    private CountDownLatch bindTimeoutLatch;
    private OutputStream outputStream;
    private MessageReader messageReader;
    private Linker linker;
    private Thread currentThread;

    private final AtomicInteger serialNumberGenerator = new AtomicInteger();
    private final Map<Integer, DeliveryInfo> deliveryInfoMap = new ConcurrentHashMap<>();

    public ConnectionManager(final String ip, final int port, final String id, final String password) {

        super();

        this.ip = ip;
        this.port = port;
        this.id = id;
        this.password = password;

        scavenger = new Scavenger(deliveryInfoMap);
        scavenger.start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @Override
    public void run() {

        currentThread = Thread.currentThread();
        do {
            try (final Socket socket = new Socket()) {
                final InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
                socket.setSoTimeout(SOCKET_TIMEOUT);
                socket.connect(socketAddress, CONNECT_TIMEOUT);
                log.debug("Connect to {}:{}", ip, port);
                outputStream = new BufferedOutputStream(socket.getOutputStream());

                messageReader = new MessageReader(socket.getInputStream(), this, this);
                messageReader.start();
                connected = true;

                bindTimeoutLatch = new CountDownLatch(1);
                sendBindMessage(id, password);
                if (bindTimeoutLatch.await(READ_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    linker = new Linker(this, this);
                    linker.start();
                    linker.join();
                } else {
                    log.error("Failed to receive bind ack message");
                    messageReader.shutdown();
                }
                messageReader.join();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RECONNECT_INTERVAL));
            } finally {
                connected = false;
                bounded = false;
            }
        } while (!terminated);
    }

    public boolean isBounded() {

        return bounded;
    }

    public void shutdown() {

        terminated = true;
        release();

        scavenger.shutdown();

        currentThread.interrupt();
        while (currentThread.isAlive()) {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }
    }

    private void release() {

        if (linker != null && linker.isAlive()) {
            linker.shutdown();
        }

        if (messageReader != null && messageReader.isAlive()) {
            messageReader.shutdown();
        }
    }

    @Override
    public void reconnect() {

        release();
    }

    @Override
    public void sendBindMessage(final String id, final String password) throws IOException {

        log.debug("Send bind message");

        sendMessage(BindMessage.builder()
                .id(id)
                .password(password)
                .build());
    }

    @Override
    public DeliverAckMessage sendDeliverMessage(final String sender, final String receiver, final String message)
            throws IOException {

        final int serialNumber = serialNumberGenerator.getAndIncrement() & 0x7fffffff;
        final CountDownLatch deliverTimeoutLatch = new CountDownLatch(1);
        deliveryInfoMap.put(serialNumber, new DeliveryInfo(deliverTimeoutLatch));

        log.debug("Send deliver message {}", serialNumber);

        sendMessage(DeliverMessage.builder()
                .tid(4098)
                .originAddress(sender)
                .destinationAddress(receiver)
                .callback(sender)
                .text(message)
                .serialNumber(serialNumber)
                .build());

        try {
            if (deliverTimeoutLatch.await(READ_TIMEOUT, TimeUnit.MILLISECONDS) &&
                    deliveryInfoMap.containsKey(serialNumber)) {
                return deliveryInfoMap.remove(serialNumber).getResult();
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.warn("Failed to receive deliver ack message {}", serialNumber);
        return null;
    }

    @Override
    public void sendReportAckMessage(final int result) throws IOException {

        log.debug("Send report ack message");

        sendMessage(ReportAckMessage.builder()
                .result(result)
                .build());
    }

    @Override
    public void sendLinkSendMessage() throws IOException {

        log.debug("Send link send message");

        sendMessage(LinkSendMessage.builder()
                .build());
    }

    private void sendMessage(final Message message) throws IOException {

        if (!connected) {
            throw new IllegalStateException("Not connected");
        }

        outputStream.write(message.marshal());
        outputStream.flush();
    }

    @Override
    public void handle(final BindAckMessage message) {

        log.debug("Receive bind ack message");

        if (connected) {
            bounded = true;
            bindTimeoutLatch.countDown();
        }
    }

    @Override
    public void handle(final DeliverAckMessage message) {

        log.debug("Receive deliver ack message {} : {}", message.getSerialNumber(), message.getResult());

        final DeliveryInfo deliveryInfo = deliveryInfoMap.get(message.getSerialNumber());
        if (deliveryInfo != null) {
            deliveryInfo.onResultReceived(message);
        }
    }

    @Override
    public void handle(final ReportMessage message) {

        log.debug("Receive report message");

        try {
            sendReportAckMessage(0);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void handle(final LinkReceiveMessage message) {

        log.debug("Receive link receive message");

        if (linker != null) {
            linker.onLinkReceived();
        }
    }

    @Override
    public void handle(final Message message) {

        log.error("Invalid message type {}", message.getType());
        reconnect();
    }
}
