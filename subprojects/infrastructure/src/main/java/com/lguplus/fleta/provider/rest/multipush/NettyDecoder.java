package com.lguplus.fleta.provider.rest.multipush;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class NettyDecoder extends ByteToMessageDecoder { // FrameDecoder -> ByteToMessageDecoder

    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf channelBuffer, List<Object> out) throws Exception {

        /**
         * Message Header Structure (64Byte)
         * ------------------------------------------------------------------------------
         *   Message ID(4)  |  Transaction ID(12)  |         Destination IP(16)
         * ------------------------------------------------------------------------------
         *      Channel ID(14)     | Reserved 1(2) |  Reserved 2(12)  |  Data Length(4)
         * ------------------------------------------------------------------------------
         */

        Channel channel = ctx.channel();

        if (!channel.isActive()) { //.isConnected()) {
            return;
        }

        channelBuffer.markReaderIndex();
        if (channelBuffer.readableBytes() < MsgEntityCommon.HEADER_SIZE) {
            return;
        }

        // Message header
        byte[] byte_MessageID = new byte[4];
        byte[] byte_TransactionID = new byte[12];
        byte[] byte_DestinationIP = new byte[16];
        byte[] byte_ChannelID = new byte[14];
        byte[] byte_Reserved1 = new byte[2];
        byte[] byte_Reserved2 = new byte[12];
        byte[] byte_Data_Length = new byte[4];

        channelBuffer.readBytes(byte_MessageID);
        channelBuffer.readBytes(byte_TransactionID);
        channelBuffer.readBytes(byte_DestinationIP);
        channelBuffer.readBytes(byte_ChannelID);
        channelBuffer.readBytes(byte_Reserved1);
        channelBuffer.readBytes(byte_Reserved2);
        channelBuffer.readBytes(byte_Data_Length);

        int messageID = ByteUtil.getint(byte_MessageID, 0);
        int dataLength = ByteUtil.getint(byte_Data_Length, 0);

        if (!MsgEntityCommon.isValidMessageType(messageID)) {
            channelBuffer.resetReaderIndex();
            return;
        }

        if (channelBuffer.readableBytes() < dataLength) {
            channelBuffer.resetReaderIndex();
            return;
        }

        // Message body
        byte[] byte_Data = new byte[dataLength];
        channelBuffer.readBytes(byte_Data);

        String transactionID = "0";
        String result = null;
        String data = null;
        String statusCode = null;

        if (messageID == MsgEntityCommon.PROCESS_STATE_REQUEST_ACK) {
            result = ByteUtil.getshort(byte_Data, 0) == 1 ? MsgEntityCommon.SUCCESS : MsgEntityCommon.FAIL;
        } else {
            result = new String(byte_Data).substring(0, 2);

            if (messageID == MsgEntityCommon.COMMAND_REQUEST_ACK) {
                byte[] byte_TransctionDate = ByteUtil.getbytes(byte_TransactionID, 0, 8);
                byte[] byte_TransctionNum = ByteUtil.getbytes(byte_TransactionID, 8, 4);

                transactionID = new String(byte_TransctionDate) + String.valueOf(ByteUtil.getint(byte_TransctionNum,0));
                data = new String(byte_Data).substring(2);
/*  //TODO.
                JSONObject jsonObj = new JSONObject(data);
                JSONObject responseObj = jsonObj.getJSONObject("response");
                statusCode = responseObj.getString("status_code");
 */
                JsonNode jsonNode = objectMapper.readTree(data);
                statusCode = jsonNode.get("response").get("status_code").asText();

            }
        }

        MessageInfo msg = new MessageInfo(messageID, transactionID, new String(byte_ChannelID), result, data, statusCode);

        out.add(msg);
    }

}
