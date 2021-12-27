package com.lguplus.fleta.provider.socket.multi;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter {

    private PushMultiClient pushMultiClient = null;

    public NettyHandler(PushMultiClient pushMultiClient) {
        this.pushMultiClient = pushMultiClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        PushMessageInfoDto message;
        if (msg instanceof PushMessageInfoDto) {
            message = (PushMessageInfoDto) msg;
        } else {
            log.error("[NettyHandler] message is not valid");
            return;
        }

        if (message.getMessageID() == MsgEntityCommon.PROCESS_STATE_REQUEST_ACK) {
            // 메시지 전송을 Sync 방식으로 작동하게 하기 위함.
            log.trace(":: NettyHandler channelRead : PROCESS_STATE_REQUEST_ACK");
            setAttachment(ctx.channel(), message);
        }
        else if (message.getMessageID() == MsgEntityCommon.COMMAND_REQUEST_ACK) {
            // Push 전송인 경우 response 결과를 임시 Map에 저장함.
            log.trace(":: NettyHandler channelRead : COMMAND_REQUEST_ACK");
            pushMultiClient.receiveAsyncMessage(message);
        }

        log.trace("[NettyHandler] id : " + ctx.channel().id() + ", messageReceived : " + message.getMessageID() + ", " +
                message.getTransactionID() + ", " + message.getResult());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.trace(":: NettyHandler channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.trace(":: NettyHandler channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

        log.error("[NettyHandler] id : " + ctx.channel().id() + ", exceptionCaught : " + e.toString());

        try {
            if (ctx.channel().isActive()) { // isConnected -> isActive
                ctx.channel().disconnect();
                ctx.channel().close();
            }
        } catch (Exception ex) {
            log.error("[NettyHandler] connection closing : {}", ex.toString());
        }
    }

    private void setAttachment(Channel channel, Object value) {
        log.trace(":: setAttachment:: {} / {}", channel.id(), ((PushMessageInfoDto)value));
        AttributeKey<Object> attrKey = AttributeKey.valueOf(NettyClient.ATTACHED_DATA_ID);
        channel.attr(attrKey).set(value);
    }
}
