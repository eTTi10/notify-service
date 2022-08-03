package com.lguplus.fleta.provider.socket.smsagent;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class MessageReader extends Thread {

    private final DataInputStream inputStream;
    private final MessageHandler messageHandler;
    private final Connector connector;

    public MessageReader(final InputStream inputStream, final MessageHandler messageHandler,
                         final Connector connector) {

        super();

        this.inputStream = new DataInputStream(new BufferedInputStream(inputStream));
        this.messageHandler = messageHandler;
        this.connector = connector;
    }

    @Override
    public void run() {

        try {
            do {
                final int type = readInt();
                final int length = readInt();
                if (MessageUnmarshaller.getRequiredBufferSize(type) != length) {
                    throw new IllegalStateException("Invalid message length for type " + type + ".");
                }

                final byte[] buffer = readBytes(length);
                handleMessage(MessageUnmarshaller.unmarshal(type, buffer));
            } while (true);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            connector.reconnect();
        }
    }

    private int readInt() throws IOException {

        return MessageUtils.readInt(readBytes(4), 0);
    }

    private byte[] readBytes(final int length) throws IOException {

        final byte[] buffer = new byte[length];
        inputStream.readFully(buffer);
        return buffer;
    }

    private void handleMessage(final Message message) {

        switch (message.getType()) {
            case BindAckMessage.TYPE:
                messageHandler.handle((BindAckMessage)message);
                break;
            case DeliverAckMessage.TYPE:
                messageHandler.handle((DeliverAckMessage)message);
                break;
            case ReportMessage.TYPE:
                messageHandler.handle((ReportMessage)message);
                break;
            case LinkReceiveMessage.TYPE:
                messageHandler.handle((LinkReceiveMessage)message);
                break;
            default:
                messageHandler.handle(message);
                break;
        }
    }

    public void shutdown() {
        try {
            inputStream.close();
        } catch (final IOException e) {
            // Do nothing.
        }
    }
}
