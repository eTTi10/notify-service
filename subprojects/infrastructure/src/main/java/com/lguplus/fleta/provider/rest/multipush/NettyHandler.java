package com.lguplus.fleta.provider.rest.multipush;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter {

    //private static final AttributeKey<MessageInfo> SERVER_STATE = AttributeKey.valueOf("MessageInfo.state");
    private MessageService messageService = null;

    public void NettyHandler() {
        messageService = MessageService.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        MessageInfo message = null;
        if (msg instanceof MessageInfo) {
            message = (MessageInfo) msg;
        } else {
            log.error("[NettyHandler] message is not valid");
            return;
        }

        if (message.getMessageID() == MsgEntityCommon.PROCESS_STATE_REQUEST_ACK) {
            // 메시지 전솔을 Sync 방식으로 작동하게 하기 위함.
            //ctx.setAttachment(message);//TODO.
            setAttachment(ctx.channel(), NettyClient.ATTACHED_DATA_ID, message);
        }
        else if (message.getMessageID() == MsgEntityCommon.COMMAND_REQUEST_ACK) {
            // Push 전송인 경우 response 결과를 임시 Map에 저장함.
            messageService.putMessageInfo(message.getTransactionID(), message);
        }

        log.debug("[NettyHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageID() + ", " +
                message.getTransactionID() + ", " + message.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

        log.error("[NettyHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());

        try {
            if (ctx.channel().isActive()) { //!e.channel().isConnected()) {
                ctx.channel().disconnect();
                ctx.channel().close();
            }
            //ctx.close();
        } catch (Exception ex) {
            log.error("[NettyHandler] connection closing : {}", ex);
        }
    }

    @AllArgsConstructor
    public static class NettyHandlerState {
        private final String channelState;
    }

    private void setAttachment(Channel channel, String key, Object value) {
        AttributeKey attrKey = AttributeKey.valueOf(key);
        channel.attr(attrKey).set(value);
    }
}
