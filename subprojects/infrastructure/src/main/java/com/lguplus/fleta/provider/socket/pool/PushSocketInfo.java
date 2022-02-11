package com.lguplus.fleta.provider.socket.pool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.FailException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.Map;

@Getter
@Slf4j
public class PushSocketInfo {
    private final Socket socket;                //Push 소켓
    private String channelID;                   //Push Header channelId
    private String destinationIp;
    private int port;

    private static final String PUSH_ENCODING = "euc-kr";
    private static final int PUSH_MSG_HEADER_LEN = 64;
    private static final String PUSH_ID_NM = "push_id";
    private static final String SUCCESS = "SC";
    private static final String FAIL = "FA";
    private static final int CHANNEL_CONNECTION_REQUEST = 1;
    private static final int CHANNEL_CONNECTION_REQUEST_ACK = 2;
    private static final int CHANNEL_PROCESS_STATE_REQUEST = 13;
    private static final int CHANNEL_PROCESS_STATE_REQUEST_ACK = 14;
    private static final int CHANNEL_RELEASE_REQUEST = 5;
    private static final int CHANNEL_RELEASE_REQUEST_ACK = 6;
    private static final int PROCESS_STATE_REQUEST = 13;
    private static final int PROCESS_STATE_REQUEST_ACK = 14;
    private static final int COMMAND_REQUEST = 15;
    private static final int COMMAND_REQUEST_ACK = 16;

    private long lastTransactionTime = Instant.now().getEpochSecond();
    private boolean isOpened = false;
    private boolean isFailure = false;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PushSocketInfo() {
        socket = new Socket();
    }

    public void openSocket(final String _host, final int _port, final int _timeout, final String _channelID, final String _destinationIp) throws IOException {

        channelID = _channelID;
        destinationIp = _destinationIp;
        port = _port;

        connectPushServer(_host, _port, _timeout);

        requestPushServer();
    }

    public void connectPushServer(final String _host, final int _port, final int _timeout) throws IOException {

        socket.connect(new InetSocketAddress(_host, _port), _timeout);
        socket.setSoTimeout(_timeout);
    }

    public void requestPushServer() throws IOException {

        sendHeaderMsg(CHANNEL_CONNECTION_REQUEST);

        //Header
        byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];
        this.socket.getInputStream().read(byteHeader, 0, PUSH_MSG_HEADER_LEN);

        //log.trace("[OpenSocket] 서버 응답 response_MessageID = " + byteToInt(byteHeader))
        //log.trace("[OpenSocket] 서버 응답 Destination IP = " + getEncodeStr(byteHeader, 16, 16))
        //log.trace("[OpenSocket] 서버 응답 ChannelId = " + getEncodeStr(byteHeader, 32, 14))

        int responseDataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);
        log.trace("[OpenSocket] 서버 응답 response_DataLength = " + responseDataLength);

        byte[] byteBody = new byte[responseDataLength];

        socket.getInputStream().read(byteBody, 0, byteBody.length);

        String responseCode = getEncodeStr(byteBody, 0, 2);
        log.trace("[OpenSocket] 서버 응답 response Code = " + responseCode);

        byte[] bResponseState = new byte[2];
        System.arraycopy(byteBody, 2, bResponseState, 0, bResponseState.length);
        short responseState = byteToShort(bResponseState);
        log.trace("[OpenSocket] 서버 응답 response Status Code = " + responseState);

        if (SUCCESS.equals(responseCode)) {
            log.trace("[OpenSocket]ChannelConnectionRequest 성공");
            log.trace("[" + channelID + "][OPEN_E][] - [SUCCESS]");

            isOpened = true;
            lastTransactionTime = Instant.now().getEpochSecond();
        }
        else {
            isOpened = false;
            log.error("requestPushServer return code failure channelId:{}", this.channelID);
        }

    }

    public boolean isInValid() {
        return (   !socket.isConnected()
                || !this.isOpened()
                || this.isFailure());
    }

    public boolean isTimeoutStatus(long closeSeconds) {
        return closeSeconds <= getLastUsedSeconds();
    }

    public long getLastUsedSeconds() {
        return Instant.now().getEpochSecond() - this.getLastTransactionTime();
    }

    public void sendHeaderMsg(int requestMsgId) throws IOException {
        sendHeaderMsg(requestMsgId, null, null);
    }

    public void sendHeaderMsg(int requestMsgId, String notiMsg, String transactionId) throws IOException {
        /* client -> server header
         * Message Header Structure (64Byte)
         * ------------------------------------------------------------------------------
         *   Message ID(4)  |  Transaction ID(12)  |  Channel ID(14)    | Reserved 1(2)
         * ------------------------------------------------------------------------------
         *             Destination IP(16)          |  Reserved 2(12)  |  Data Length(4)
         * ------------------------------------------------------------------------------
         */
        byte[] byteArrayDatas = new byte[0];
        if(COMMAND_REQUEST == requestMsgId) {
            byteArrayDatas = notiMsg.getBytes(PUSH_ENCODING);
        }

        byte[] sendHeader = new byte[PUSH_MSG_HEADER_LEN + byteArrayDatas.length];
        System.arraycopy(Ints.toByteArray(requestMsgId), 0, sendHeader, 0, 4);
        if(COMMAND_REQUEST == requestMsgId) {
            System.arraycopy(transactionId.getBytes(PUSH_ENCODING), 0, sendHeader, 4, 12);   //Transaction Id
        }
        System.arraycopy(this.channelID.getBytes(PUSH_ENCODING), 0, sendHeader, 16, 14);
        System.arraycopy(destinationIp.getBytes(PUSH_ENCODING), 0, sendHeader, 32, destinationIp.getBytes(PUSH_ENCODING).length);
        System.arraycopy(Ints.toByteArray(byteArrayDatas.length), 0, sendHeader, 60, 4);
        if(COMMAND_REQUEST == requestMsgId) {
            System.arraycopy(byteArrayDatas, 0, sendHeader, 64, byteArrayDatas.length); //Data
        }

        this.socket.getOutputStream().write(sendHeader);
        this.socket.getOutputStream().flush();

    }

    public PushResponseDto sendPushNotice(final Map<String, String> pushBody) throws IOException {

        //Send G/W
        ObjectNode oNode = objectMapper.createObjectNode();
        oNode.set("request", objectMapper.valueToTree(pushBody));
        String jsonStr = oNode.toString();

        sendHeaderMsg(COMMAND_REQUEST, jsonStr, pushBody.get(PUSH_ID_NM));

        //Recv Header
        PushRcvHeaderVo pushRcvHeaderVo = recvPushMessageHeader();

        //Parse Message
        return recvPushMessageBody(pushRcvHeaderVo);

    }

    private PushRcvHeaderVo recvPushMessageHeader()  throws IOException {
        /* server -> client header
         * Message Header Structure (64Byte)
         * ------------------------------------------------------------------------------
         *   Message ID(4)  |  Transaction ID(12)  |         Destination IP(16)
         * ------------------------------------------------------------------------------
         *      Channel ID(14)     | Reserved 1(2) |  Reserved 2(12)  |  Data Length(4)
         * ------------------------------------------------------------------------------
         */

        InputStream inputStream = socket.getInputStream();

        //Header
        byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];

        inputStream.read(byteHeader, 0, PUSH_MSG_HEADER_LEN);

        log.trace("[setNoti] 서버 응답 response_MessageID = " + byteToInt(byteHeader));
        log.trace("[setNoti] 서버 응답 Transaction ID = " + getEncodeStr(byteHeader, 4, 12));
        log.trace("[setNoti] 서버 응답 Destination IP = " + getEncodeStr(byteHeader, 16, 16));
        log.trace("[setNoti] 서버 응답 ChannelId = " + getEncodeStr(byteHeader, 32, 14));

        int responseDataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);
        log.trace("[setNoti] 서버 응답 responseDataLength = " + responseDataLength);

        log.trace("======================= Body =========================");
        byte[] byteBody = new byte[responseDataLength];

        inputStream.read(byteBody, 0, byteBody.length);

        byte[] bResponseCode = new byte[2];
        System.arraycopy(byteBody, 0, bResponseCode, 0, bResponseCode.length);
        String responseCode = new String(bResponseCode, PUSH_ENCODING);
        log.trace("[setNoti] 서버 응답 response Code = " + responseCode);

        return PushRcvHeaderVo.builder().status(responseCode).recvLength(responseDataLength).recvBuffer(byteBody).build();

    }

    private PushResponseDto recvPushMessageBody(PushRcvHeaderVo pushRcvHeaderVo) throws IOException {

        PushResponseDto responseDto = PushResponseDto.builder().responseCode(pushRcvHeaderVo.getStatus()).build();

        switch (pushRcvHeaderVo.getStatus()) {
            case SUCCESS :
                byte[] bJsonMsg = new byte[pushRcvHeaderVo.getRecvLength() - 2];
                System.arraycopy(pushRcvHeaderVo.getRecvBuffer(), 2, bJsonMsg, 0, bJsonMsg.length);
                String retJsonMsg = new String(bJsonMsg, PUSH_ENCODING);
                retJsonMsg = retJsonMsg.replaceAll("(\r\n|\n)", "");

                PushRcvStatusMsgWrapperVo msgWrapperVo = objectMapper.readValue(retJsonMsg, PushRcvStatusMsgWrapperVo.class);

                //log.debug("msgWrapperVo: {} , {}", msgWrapperVo, msgWrapperVo.getResponse())
                lastTransactionTime = Instant.now().getEpochSecond();

                responseDto.setStatusCode(msgWrapperVo.getResponse().getStatusCode());
                responseDto.setStatusMsg(msgWrapperVo.getResponse().getStatusMsg());
                return responseDto;

            case FAIL :
                log.error("[setNoti] 서버 응답 FA ");
                break;

            default:
                log.error("[setNoti] Unknown 응답 ");
                break;
        }

        isFailure = true;
        throw new FailException();
    }

    public void isServerInValidStatus() {

        try
        {
            sendHeaderMsg(CHANNEL_PROCESS_STATE_REQUEST);

            //Header
            byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];
            this.socket.getInputStream().read(byteHeader, 0, PUSH_MSG_HEADER_LEN);

            log.trace("[isServerInValidStatus] 서버 응답 response_MessageID = " + byteToInt(byteHeader));
            //log.trace("[isServerInValidStatus] 서버 응답 Destination IP = " + getEncodeStr(byteHeader, 16, 16))
            //log.trace("[isServerInValidStatus] 서버 응답 ChannelId = " + getEncodeStr(byteHeader, 32, 14))

            int responseDataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);
            log.trace("[isServerInValidStatus] 서버 응답 response_DataLength = " + responseDataLength);

            byte[] byteBody = new byte[responseDataLength];

            socket.getInputStream().read(byteBody, 0, byteBody.length);

            short healthStatus = byteToShort(byteBody);
            //SUCCESS 1, Fail 0
            if (healthStatus == 1) {
                log.debug("[" + channelID + "][Health Check] - [SUCCESS]");
                lastTransactionTime = Instant.now().getEpochSecond();
            }
            else {
                log.error("[" + channelID + "][Health Check] - [Failure]");
            }

        } catch (IOException e) {
            log.error("isServerInValidStatus error: {}", e.getMessage());
        }
    }

    public void closeSocket()  {

        try {
            closeSocketResource();
        }
        catch (IOException e) {
            log.error("closeSocket io-exception : {}", e.getMessage());
        }

    }

    public void closeSocketResource() throws IOException {

        try (socket) {
            this.isOpened = false;
            log.debug("[===>closeSocket] {}", this);
        }

    }

    public short byteToShort(byte[] src) {
        return (short) ((src[0] & 0xff) << 8 | src[1] & 0xff);
    }

    public int byteToInt(byte[] src, int... b) {
        int offset = 0;
        if(b.length > 0) {
            offset = b[0];
        }
        return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
    }

    public String getEncodeStr(byte[] src, int offset, int length) throws IOException {
        byte[] tmpArray = new byte[length];
        System.arraycopy(src, offset, tmpArray, 0, length);
        return new String(tmpArray, PUSH_ENCODING);
    }

    public String toString() {
        return channelID + ":"  + port + ",isOpened:" + isOpened + ",isFailure:" + isFailure + ",time:" + (Instant.now().getEpochSecond() - this.lastTransactionTime);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    static class PushRcvHeaderVo {
        private String status;
        private int recvLength;
        private byte[] recvBuffer;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    static class PushRcvStatusMsgVo {
        @JsonProperty("msg_id")
        private String messageId;

        @JsonProperty("push_id")
        private String pushId;

        @JsonProperty("status_code")
        private String statusCode;

        @JsonProperty("statusmsg")
        private String statusMsg;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    static class PushRcvStatusMsgWrapperVo {
        @JsonProperty("response")
        private PushRcvStatusMsgVo response;
    }
}
