package com.lguplus.fleta.provider.rest.multipush;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class NettyEncoder extends MessageToMessageEncoder<MessageInfo> { // OneToOneEncoder -> MessageToMessageEncoder

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageInfo msg, List<Object> out) throws Exception {

        /**
         * Message Header Structure (64Byte)
         * ------------------------------------------------------------------------------
         *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
         * ------------------------------------------------------------------------------
         *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
         * ------------------------------------------------------------------------------
         */

        Channel channel = ctx.channel();

        MessageInfo message = null;
        if (msg instanceof MessageInfo) {
            message = (MessageInfo) msg;
        } else {
            return;
        }

        byte[] byte_MessageID = ByteUtil.int2byte(message.getMessageID());
        byte[] byte_TransactionID = new byte[12];
        byte[] byte_ChannelID = message.getChannelID().getBytes();
        byte[] byte_Reserved1 = new byte[2];
        byte[] byte_DestinationIP = new byte[16];
        byte[] byte_Reserved2 = new byte[12];
        byte[] byte_Data = new byte[0];
        byte[] byte_Data_Length = ByteUtil.int2byte(0);

        //TODO. Destination IP
        ByteUtil.setbytes(byte_DestinationIP, 0, "127.0.0.1".getBytes());//PushSocketListComm.destinationIP.getBytes());

        // Message가 CommandRequest인 경우
        if (message.getMessageID() == MsgEntityCommon.COMMAND_REQUEST) {
            byte_TransactionID = message.getBTransactionID();// .getbTransactionID();
            byte_Data = message.getData().getBytes();
            byte_Data_Length = ByteUtil.int2byte(message.getData().getBytes().length);
        }

        int byteTotalLen = MsgEntityCommon.HEADER_SIZE + byte_Data.length;

        byte[] byteTotalData = new byte[byteTotalLen];
        ByteUtil.setbytes(byteTotalData, 0, byte_MessageID);
        ByteUtil.setbytes(byteTotalData, 4, byte_TransactionID);
        ByteUtil.setbytes(byteTotalData, 16, byte_ChannelID);
        ByteUtil.setbytes(byteTotalData, 30, byte_Reserved1);
        ByteUtil.setbytes(byteTotalData, 32, byte_DestinationIP);
        ByteUtil.setbytes(byteTotalData, 48, byte_Reserved2);
        ByteUtil.setbytes(byteTotalData, 60, byte_Data_Length);
        ByteUtil.setbytes(byteTotalData, 64, byte_Data);

        /*
        ChannelBuffer channelBuffer = ChannelBuffers.buffer(MsgEntityCommon.HEADER_SIZE + byte_Data.length);
        channelBuffer.writeBytes(byteTotalData);
        */

        ByteBuf byteBuf = Unpooled.copiedBuffer(byteTotalData);
        out.add(byteBuf);
    }

}