package com.lguplus.fleta.provider.socket.smsagent;

import java.io.IOException;

public interface MessageSender {

    void sendBindMessage(String id, String password) throws IOException;
    void sendReportAckMessage(int result) throws IOException;
    void sendLinkSendMessage() throws IOException;

    DeliverAckMessage sendDeliverMessage(String sender, String receiver, String message) throws IOException;
}
