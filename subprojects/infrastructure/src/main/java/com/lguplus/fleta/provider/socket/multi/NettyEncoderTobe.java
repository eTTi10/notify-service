package com.lguplus.fleta.provider.socket.multi;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Client -> Server
 * Client에서 송신하는 데이터(MessageInfo)를 byte[]로 변환한다.
 */
@NoArgsConstructor
public class NettyEncoderTobe extends MessageToMessageEncoder<MessageInfo> { // OneToOneEncoder -> MessageToMessageEncoder

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

        //Test
        Charset cs = Charset.forName("euc-kr");
        //Default Order Big Endian
        ByteBuf buffer = Unpooled.buffer(MsgEntityCommon.HEADER_SIZE + message.getData().getBytes(cs).length);
        int idx = 0;
        buffer.setInt(idx, message.getMessageID());
        idx += 4;
        buffer.setCharSequence(idx, message.getTransactionDate(), cs);
        idx += 8;
        buffer.setInt(idx, message.getTransactionSeq());
        idx += 4;
        buffer.setCharSequence(idx, message.getChannelID(), cs);
        idx += 14;
        buffer.setShort(idx, 0);//Reservation1
        idx += 2;
        buffer.setBytes(idx, convertStrToBytes("127.0.0.1", 16, cs));
        idx += 16;
        buffer.setBytes(idx, new byte[12]);//Reservation2
        idx += 12;
        buffer.setInt(idx, message.getData().getBytes(cs).length);
        idx += 4;
        buffer.setCharSequence(idx, message.getData(), cs);

        //out.add(buffer);
    }

    private byte[] convertStrToBytes(String src, int length, Charset cs) {
        byte[] dst = new byte[length];
        System.arraycopy(src.getBytes(cs), 0, dst, 0, src.getBytes(cs).length);
        return dst;
    }
}