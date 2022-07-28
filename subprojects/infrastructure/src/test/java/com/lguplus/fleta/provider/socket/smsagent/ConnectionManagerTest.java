package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
class ConnectionManagerTest {

    @Test
    void testRun() {

        try (MockedConstruction<Socket> ignored = mockConstruction(Socket.class, (mock, context) -> {
                    doNothing().when(mock).connect(any(), any());
                    doReturn(new ByteArrayInputStream(new byte[0])).when(mock).getInputStream();
                    doReturn(new ByteArrayOutputStream()).when(mock).getOutputStream();});
             MockedConstruction<CountDownLatch> ignored2 = mockConstruction(CountDownLatch.class, (mock, context) ->
                    doReturn(true).when(mock).await(any(), any()))
        ) {
            ConnectionManager connectionManager = new ConnectionManager("localhost", 0, null, null);
            assertDoesNotThrow(connectionManager::start);

            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
            connectionManager.shutdown();
        }
    }
}