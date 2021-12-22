package com.lguplus.fleta.provider.socket.multi;

import com.google.common.primitives.Ints;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Client -> Server
 * Client에서 송신하는 데이터(MessageInfo)를 byte[]로 변환한다.
 */
@Slf4j
@NoArgsConstructor
public class NettyEncoder extends MessageToMessageEncoder<MessageInfo> {
    // OneToOneEncoder -> MessageToMessageEncoder

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageInfo message, List<Object> out) throws Exception {

        /**
         * Message Header Structure (64Byte)
         * ------------------------------------------------------------------------------
         *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
         * ------------------------------------------------------------------------------
         *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
         * ------------------------------------------------------------------------------
         */

        byte[] dataInfo = message.getData().getBytes(MsgEntityCommon.PUSH_ENCODING);

        byte[] byteTotalData = new byte[MsgEntityCommon.PUSH_MSG_HEADER_LEN + dataInfo.length];
        System.arraycopy(Ints.toByteArray(message.getMessageID()), 0, byteTotalData, 0, 4);                    //Message Id
        System.arraycopy(message.getTransactionID().getBytes(MsgEntityCommon.PUSH_ENCODING), 0, byteTotalData, 4, 12);   //Transaction Id
        System.arraycopy(message.getChannelID().getBytes(MsgEntityCommon.PUSH_ENCODING), 0, byteTotalData, 16, 14);             //Channel Id
        System.arraycopy(message.getDestIp().getBytes(MsgEntityCommon.PUSH_ENCODING), 0, byteTotalData, 32, message.getDestIp().getBytes(MsgEntityCommon.PUSH_ENCODING).length);//Destination IP
        System.arraycopy(Ints.toByteArray(dataInfo.length), 0, byteTotalData, 60, 4);                 //Data Length
        System.arraycopy(dataInfo, 0, byteTotalData, 64, dataInfo.length);

        log.trace("sendHeader Len =" + byteTotalData.length);

        ByteBuf byteBuf = Unpooled.copiedBuffer(byteTotalData);
        out.add(byteBuf);
    }

}