package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Server -> Client
 * Server에서 수신한 데이터를 MessageInfo로 변환한다.
 */
@NoArgsConstructor
public class NettyDecoder extends ByteToMessageDecoder { // FrameDecoder -> ByteToMessageDecoder

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String RESPONSE_ID_NM = "response";
    private static final String RESPONSE_STATUS_CD = "status_code";

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

        if (!channel.isActive()) {
            return;
        }

        channelBuffer.markReaderIndex();
        if (channelBuffer.readableBytes() < MsgEntityCommon.PUSH_MSG_HEADER_LEN) {
            return;
        }

        // Message header
        byte[] byteHeader = new byte[MsgEntityCommon.PUSH_MSG_HEADER_LEN];
        channelBuffer.readBytes(byteHeader);

        int messageID = byteToInt(byteHeader, 0);
        int dataLength = byteToInt(byteHeader, MsgEntityCommon.PUSH_MSG_HEADER_LEN - 4);

        if (!MsgEntityCommon.isValidMessageType(messageID)) {
            channelBuffer.resetReaderIndex();
            return;
        }

        if (channelBuffer.readableBytes() < dataLength) {
            channelBuffer.resetReaderIndex();
            return;
        }

        // Message body
        byte[] byteData = new byte[dataLength];
        channelBuffer.readBytes(byteData);

        String transactionID = "0";
        String result;
        String data = null;
        String statusCode = null;
        String channelId = new String(byteHeader, 32, 14, MsgEntityCommon.PUSH_ENCODING);

        if (messageID == MsgEntityCommon.PROCESS_STATE_REQUEST_ACK) {
            result = byteToShort(byteData) == 1 ? MsgEntityCommon.SUCCESS : MsgEntityCommon.FAIL;
        } else {
            result = new String(byteData,0, 2, MsgEntityCommon.PUSH_ENCODING);

            if (messageID == MsgEntityCommon.COMMAND_REQUEST_ACK) {

                transactionID = new String(byteHeader, 4, 12, MsgEntityCommon.PUSH_ENCODING);
                data = new String(byteData,2, byteData.length - 2, MsgEntityCommon.PUSH_ENCODING);

                JsonNode jsonNodeR = objectMapper.readTree(data);

                if(jsonNodeR != null && jsonNodeR.has(RESPONSE_ID_NM) && jsonNodeR.get(RESPONSE_ID_NM).has(RESPONSE_STATUS_CD)) {
                    statusCode = jsonNodeR.get(RESPONSE_ID_NM).get(RESPONSE_STATUS_CD).asText();
                }

            }
        }

        MessageInfo msg = MessageInfo.builder()
                    .messageID(messageID)
                    .transactionID(transactionID)
                    .channelID(channelId)
                    .result(result)
                    .data(data)
                    .statusCode(statusCode)
                .build();

        out.add(msg);
    }

    private short byteToShort(byte[] src) {
        return (short) ((src[0] & 0xff) << 8 | src[1] & 0xff);
    }

    private int byteToInt(byte[] src, int... b) {
        int offset = 0;
        if(b.length > 0) {
            offset = b[0];
        }
        return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
    }

}
