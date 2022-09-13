package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
class LinkerTest {

    MessageSender messageSender = new MessageSender() {
        @Override
        public void sendBindMessage(String id, String password) {}

        @Override
        public void sendReportAckMessage(int result) {}

        @Override
        public void sendLinkSendMessage() {}

        @Override
        public DeliverAckMessage sendDeliverMessage(String sender, String receiver, String message) {
            return null;
        }
    };

    Connector connector = () -> {};

    @Test
    void testRun() {
        Linker linker = spy(new Linker(messageSender, connector));
        try (MockedConstruction<CountDownLatch> ignore = mockConstruction(CountDownLatch.class, (mock, context) ->
                doReturn(true).when(mock).await(anyLong(), any())
        )) {
            new Thread(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                linker.shutdown();
            }).start();

            assertDoesNotThrow(linker::run);
        }
    }

    @Test
    void testRunReadTimeout() {
        Linker linker = spy(new Linker(messageSender, connector));
        try (MockedConstruction<CountDownLatch> ignore = mockConstruction(CountDownLatch.class, (mock, context) ->
                doReturn(false).when(mock).await(anyLong(), any())
        )) {
            new Thread(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                linker.shutdown();
            }).start();

            assertDoesNotThrow(linker::run);
        }
    }

    @Test
    void testRunInterruptedException() {
        Linker linker = spy(new Linker(messageSender, connector));
        try (MockedConstruction<CountDownLatch> ignore = mockConstruction(CountDownLatch.class, (mock, context) ->
                doThrow(InterruptedException.class).when(mock).await(anyLong(), any())
        )) {
            new Thread(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                linker.shutdown();
            }).start();

            assertDoesNotThrow(linker::run);
        }
    }

    @Test
    void testOnLinkReceived() {
        Linker linker = spy(new Linker(messageSender, connector));
        try (MockedConstruction<CountDownLatch> ignore = mockConstruction(CountDownLatch.class, (mock, context) ->
                doReturn(true).when(mock).await(anyLong(), any())
        )) {
            new Thread(() -> {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                linker.shutdown();
            }).start();

            assertDoesNotThrow(linker::run);
            assertDoesNotThrow(linker::onLinkReceived);
        }
    }

    @Test
    void testOnLinkReceivedOnNotRunning() {
        Linker linker = new Linker(messageSender, connector);
        assertDoesNotThrow(linker::onLinkReceived);
    }
}
