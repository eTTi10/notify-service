package com.lguplus.fleta.provider.socket.smsagent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MessageUnmarshaller {

    private static final Map<Integer, Message> availableMessages = List.of(
            new BindMessage(), new BindAckMessage(), new DeliverMessage(), new DeliverAckMessage(),
            new ReportMessage(), new ReportAckMessage(), new LinkSendMessage(), new LinkReceiveMessage()
    ).stream().collect(Collectors.toMap(Message::getType, Function.identity()));


    private MessageUnmarshaller() {

        // Do nothing.
    }

    private static void checkType(final int type) {

        if (!availableMessages.containsKey(type)) {
            throw new IllegalArgumentException("Undefined message type.");
        }
    }

    public static int getRequiredBufferSize(final int type) {

        checkType(type);

        return availableMessages.get(type).getLength();
    }

    public static <T extends Message> T unmarshal(final int type, final byte[] buffer) {

        checkType(type);

        try {
            final T message = (T)availableMessages.get(type).getClass().getConstructor().newInstance();
            message.unmarshal(buffer);
            return message;
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e) {
            throw new IllegalStateException("Failed to unmarshal message.", e);
        }
    }
}
