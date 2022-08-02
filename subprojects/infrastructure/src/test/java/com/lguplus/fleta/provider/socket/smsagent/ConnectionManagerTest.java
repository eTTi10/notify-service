package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
class ConnectionManagerTest {

    static NettySmsAgentServer server;

    @BeforeAll
    static void setup() {
        server = new NettySmsAgentServer();
        server.runServer(8999);
    }

    @AfterAll
    static void release() {
        server.stopServer();
    }

    @Test
    void testRun() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        ReflectionTestUtils.invokeMethod(connectionManager, "release");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        connectionManager.shutdown();
    }

    @Test
    void testRunBindAckTimeout() {
        ConnectionManager connectionManager = spy(new ConnectionManager("localhost", 8999, "test", "test"));
        doNothing().when(connectionManager).handle(any(BindAckMessage.class));
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(7));

        boolean bounded = connectionManager.isBounded();
        assertFalse(bounded);

        connectionManager.shutdown();
    }

    @Test
    void testRunConnectionFail() {
        ConnectionManager connectionManager = spy(new ConnectionManager("localhost", 8999, "test", "test"));
        new Thread(() -> {
            try (MockedConstruction<Socket> ignore = mockConstruction(Socket.class, (mock, context) ->
                    doThrow(new IOException()).when(mock).connect(any(), anyInt())
            )) {
                assertDoesNotThrow(connectionManager::run);
            }
        }).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertFalse(bounded);

        connectionManager.shutdown();
    }

    @Test
    void testSendDeliverMessage() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        DeliverAckMessage result = connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트");
        assertThat(result.getResult()).isZero();

        connectionManager.shutdown();
    }

    @Test
    void testSendDeliverMessageReadTimeout() throws IOException {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        try (MockedConstruction<CountDownLatch> ignored = mockConstruction(CountDownLatch.class, (mock, context) ->
                doReturn(false).when(mock).await(anyLong(), any())
        )) {
            DeliverAckMessage result = connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트");
            assertThat(result).isNull();

            connectionManager.shutdown();
        }
    }

    @Test
    void testSendDeliverMessageNoDeliveryInfo() throws IOException {
        Map<Integer, DeliveryInfo> deliveryInfoMap = spy(new ConcurrentHashMap<>());
        doReturn(false).when(deliveryInfoMap).containsKey(anyInt());

        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        ReflectionTestUtils.setField(connectionManager, "deliveryInfoMap", deliveryInfoMap);
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        DeliverAckMessage result = connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트");
        assertThat(result).isNull();

        connectionManager.shutdown();
    }

    @Test
    void testSendDeliverMessageInterruptedException() throws IOException {
        ConnectionManager connectionManager = spy(new ConnectionManager("localhost", 8999, "test", "test"));
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        Thread currentThread = Thread.currentThread();
        new Thread(() -> {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
            currentThread.interrupt();
        }).start();

        doNothing().when(connectionManager).handle(any(DeliverAckMessage.class));
        DeliverAckMessage result = connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트");
        assertThat(result).isNull();

        connectionManager.shutdown();
    }

    @Test
    void testSendMessageOnNotConnected() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 9999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertFalse(bounded);

        assertThrows(IllegalStateException.class, () -> connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트"));

        connectionManager.shutdown();
    }

    @Test
    void testHandleBindAckMessageOnNotConnected() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 9999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertFalse(bounded);

        assertDoesNotThrow(() -> connectionManager.handle(BindAckMessage.builder().build()));
        connectionManager.shutdown();
    }

    @Test
    void testHandleDeliverAckMessageNoDeliveryInfo() throws IOException {
        Map<Integer, DeliveryInfo> deliveryInfoMap = spy(new ConcurrentHashMap<>());
        doReturn(null).when(deliveryInfoMap).get(anyInt());

        ConnectionManager connectionManager = spy(new ConnectionManager("localhost", 8999, "test", "test"));
        ReflectionTestUtils.setField(connectionManager, "deliveryInfoMap", deliveryInfoMap);
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        DeliverAckMessage result = connectionManager.sendDeliverMessage("01012345678", "01023456789", "테스트");
        assertThat(result).isNull();

        connectionManager.shutdown();
    }

    @Test
    void testHandleReportMessage() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        assertDoesNotThrow(() -> connectionManager.handle(ReportMessage.builder().build()));

        connectionManager.shutdown();
    }

    @Test
    void testHandleReportMessageIOExceptionOnSendReportAckMessage() throws IOException {
        ConnectionManager connectionManager = spy(new ConnectionManager("localhost", 8999, "test", "test"));
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        doThrow(IOException.class).when(connectionManager).sendReportAckMessage(anyInt());
        assertDoesNotThrow(() -> connectionManager.handle(ReportMessage.builder().build()));

        connectionManager.shutdown();
    }

    @Test
    void testHandleLinkReceiveMessageOnNotConnected() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 9999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertFalse(bounded);

        assertDoesNotThrow(() -> connectionManager.handle(LinkReceiveMessage.builder().build()));
        connectionManager.shutdown();
    }

    @Test
    void testHandleMessageInvalidType() {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 8999, "test", "test");
        new Thread(() -> assertDoesNotThrow(connectionManager::run)).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));

        boolean bounded = connectionManager.isBounded();
        assertTrue(bounded);

        Message message = spy(LinkSendMessage.builder().build());
        doReturn(-1).when(message).getType();

        assertDoesNotThrow(() -> connectionManager.handle(message));
        connectionManager.shutdown();
    }
}
