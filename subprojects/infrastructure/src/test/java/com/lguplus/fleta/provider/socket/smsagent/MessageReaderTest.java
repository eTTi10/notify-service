package com.lguplus.fleta.provider.socket.smsagent;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
class MessageReaderTest {

    static ServerSocket serverSocket;

    MessageHandler messageHandler = new MessageHandler() {
        @Override
        public void handle(BindAckMessage message) {}

        @Override
        public void handle(DeliverAckMessage message) {}

        @Override
        public void handle(ReportMessage message) {}

        @Override
        public void handle(LinkReceiveMessage message) {}

        @Override
        public void handle(Message message) {}
    };

    Connector connector = () -> {};

    @BeforeAll
    static void setup() throws IOException {
        serverSocket = new ServerSocket(8999);
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    OutputStream outputStream = socket.getOutputStream();
                    new MessageReader(socket.getInputStream(), new MessageHandler() {
                        @SneakyThrows
                        @Override
                        public void handle(BindAckMessage message) {
                            outputStream.write(message.marshal());
                        }

                        @Override
                        public void handle(DeliverAckMessage message) {}

                        @Override
                        public void handle(ReportMessage message) {}

                        @SneakyThrows
                        @Override
                        public void handle(LinkReceiveMessage message) {
                            byte[] buffer = new byte[8];
                            Arrays.fill(buffer, (byte)1);
                            System.arraycopy(message.marshal(), 0, buffer, 0, 4);
                            outputStream.write(buffer);
                        }

                        @Override
                        public void handle(Message message) {}
                    }, () -> {}).start();
                } catch (Exception e) {
                    // Do nothing.
                }
            }
        }).start();
    }

    @AfterAll
    static void release() throws IOException {
        serverSocket.close();
    }

    @Test
    void testRun() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);
        new Thread(() -> assertDoesNotThrow(messageReader::run)).start();

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(BindAckMessage.builder().prefix("019").build().marshal());
        outputStream.flush();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        socket.close();
    }

    @Test
    void testRunInvalidMessageLength() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);
        new Thread(() -> assertDoesNotThrow(messageReader::run)).start();

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(LinkReceiveMessage.builder().build().marshal());
        outputStream.flush();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        socket.close();
    }

    @Test
    void testHandleBindAckMessage() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(messageReader, "handleMessage", BindAckMessage.builder().build()));

        socket.close();
    }

    @Test
    void testHandleDeliverAckMessage() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(messageReader, "handleMessage", DeliverAckMessage.builder().build()));

        socket.close();
    }

    @Test
    void testHandleReportMessage() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(messageReader, "handleMessage", ReportMessage.builder().build()));

        socket.close();
    }

    @Test
    void testHandleLinkReceiveMessage() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(messageReader, "handleMessage", LinkReceiveMessage.builder().build()));

        socket.close();
    }

    @Test
    void testHandleUndefinedMessage() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);
        Message message = spy(LinkReceiveMessage.builder().build());
        doReturn(-1).when(message).getType();

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(messageReader, "handleMessage", message));

        socket.close();
    }

    @Test
    void testShutdown() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        MessageReader messageReader = new MessageReader(socket.getInputStream(), messageHandler, connector);

        assertDoesNotThrow(messageReader::shutdown);

        socket.close();
    }

    @Test
    void testShutdownInvalidInputStream() throws IOException {
        Socket socket = new Socket("localhost", 8999);
        InputStream inputStream = spy(socket.getInputStream());
        doThrow(IOException.class).when(inputStream).close();
        MessageReader messageReader = new MessageReader(inputStream, messageHandler, connector);

        assertDoesNotThrow(messageReader::shutdown);

        socket.close();
    }
}
