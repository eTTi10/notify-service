package com.lguplus.fleta.provider.socket.smsagent;

public interface MessageHandler {

    void handle(BindAckMessage message);
    void handle(DeliverAckMessage message);
    void handle(ReportMessage message);
    void handle(LinkReceiveMessage message);
    void handle(Message message);
}
