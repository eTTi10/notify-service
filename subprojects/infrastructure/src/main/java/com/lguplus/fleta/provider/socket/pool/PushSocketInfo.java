package com.lguplus.fleta.provider.socket.pool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class PushSocketInfo {
    private Socket pushSocket;                  //Push 소켓
    private InputStream pushIn;                 //Push InputStream
    private OutputStream pushOut;               //Push OutputStream

    private String channelID;                   //Push Header channelID
    private int channelNum;                     //Push Header channelNum
    private String destIp;

    private static final String PUSH_ENCODING = "euc-kr";
    private static final int PUSH_MSG_HEADER_LEN = 64;
    private static final String PUSH_ID_NM = "push_id";
    private static final String RESPONSE_ID_NM = "response";
    private static final String RESPONSE_STATUS_CD = "status_code";
    private static final String RESPONSE_STATUS_MSG = "statusmsg";

    private long socketTime = -1;
    private boolean isOpened = false;
    private int failCount = 0;
    private ObjectMapper objectMapper;

    public PushSocketInfo(Socket pushSocket) {
        this.pushSocket = pushSocket;
        this.objectMapper = new ObjectMapper();
    }

    public void openSocket(final String _host, final int _port, final int _timeout, final String _channelID, final String _destIp) throws PushBizException {
        try
        {
            SocketAddress adder = new InetSocketAddress(_host, _port);
            this.getPushSocket().connect(adder, _timeout);

            this.setPushIn(this.getPushSocket().getInputStream());
            this.setPushOut(this.getPushSocket().getOutputStream());

            this.setChannelID(_channelID);
            this.getPushSocket().setSoTimeout(_timeout);
            this.setDestIp(_destIp);

            byte[] byteDestinationIP = new byte[16];
            System.arraycopy(destIp.getBytes(PUSH_ENCODING), 0, byteDestinationIP, 0, destIp.getBytes(PUSH_ENCODING).length);

            log.trace("[OpenSocket]byteTotalLen=" + PUSH_MSG_HEADER_LEN); //64

            byte[] sendHeader = new byte[PUSH_MSG_HEADER_LEN];
            System.arraycopy(Ints.toByteArray(1), 0, sendHeader, 0, 4);
            System.arraycopy(this.getChannelID().getBytes(PUSH_ENCODING), 0, sendHeader, 16, 14);
            System.arraycopy(byteDestinationIP, 0, sendHeader, 32, 16);
            System.arraycopy(Ints.toByteArray(0), 0, sendHeader, 60, 4);

            this.getPushOut().write(sendHeader);
            this.getPushOut().flush();

            log.trace("======================= Header =========================");
            //Header
            int readBits;

            byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];
            readBits = this.getPushIn().read(byteHeader, 0, PUSH_MSG_HEADER_LEN);
            if(readBits != PUSH_MSG_HEADER_LEN) {
                isOpened = false;
                failCount++;
                throw new PushBizException(-200, "", new Exception());
            }

            log.trace("[OpenSocket] 서버 응답 response_MessageID = " + byteToInt(byteHeader));
            log.trace("[OpenSocket] 서버 응답 Destination IP = " + getEncodeStr(byteHeader, 16, 16));
            log.trace("[OpenSocket] 서버 응답 ChannelId = " + getEncodeStr(byteHeader, 32, 14));

            int responseDataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);
            log.trace("[OpenSocket] 서버 응답 response_DataLength = " + responseDataLength);

            log.trace("======================= Body =========================");
            byte[] byteBody = new byte[responseDataLength];

            readBits = this.getPushIn().read(byteBody, 0, byteBody.length);
            if(readBits != responseDataLength) {
                isOpened = false;
                failCount++;
                throw new PushBizException(-200, "", new Exception());
            }

            String responseCode = getEncodeStr(byteBody, 0, 2);
            log.trace("[OpenSocket] 서버 응답 response Code = " + responseCode);

            byte[] bResponseState = new byte[2];
            System.arraycopy(byteBody, 2, bResponseState, 0, bResponseState.length);
            short responseState = byteToShort(bResponseState);
            log.trace("[OpenSocket] 서버 응답 response Status Code = " + responseState);

            if ("SC".equals(responseCode)) {
                log.trace("[OpenSocket]ChannelConnectionRequest 성공");
                log.trace("[" + this.getChannelID() + "][OPEN_E][] - [SUCCESS]");

                isOpened = true;
                socketTime = Instant.now().getEpochSecond();
            } else {
                log.trace("[OpenSocket]ChannelConnectionRequest 실패");
                isOpened = false;
                failCount++;
            }
        } catch (ConnectException e) {
            log.trace("[setNoti][ConnectException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-100, e.getClass().getName(), e);
        } catch (java.net.SocketException e) {
            log.trace("[setNoti][SocketException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-200, e.getClass().getName(), e);
        } catch (SocketTimeoutException e) {
            log.trace("[setNoti][SocketTimeoutException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-300, e.getClass().getName(), e);
        } catch (Exception e) {
            log.trace("[setNoti][Exception][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-400, e.getClass().getName(), e);
        }
    }

    public PushResponseDto sendPushNotice(final Map<String, String> pushBody) throws PushBizException {

        //Send G/W
        sendPushMessage(pushBody);

        //Recv Header
        PushRcvHeaderVo pushRcvHeaderVo = recvPushMessageHeader();

        //Parse Message
        return recvPushMessageBody(pushRcvHeaderVo);
    }

    private void sendPushMessage(final Map<String, String> pushBody) throws PushBizException {

        try
        {
            ObjectNode oNode = objectMapper.createObjectNode();
            oNode.set("request", objectMapper.valueToTree(pushBody));
            String jsonStr = oNode.toString();

            byte[] byteDATA = jsonStr.getBytes(PUSH_ENCODING);
            log.trace("[setNoti] json = {}, {}", byteDATA.length, jsonStr);

            byte[] sendHeader = new byte[PUSH_MSG_HEADER_LEN + byteDATA.length];
            System.arraycopy(Ints.toByteArray(15), 0, sendHeader, 0, 4);                    //Message Id
            System.arraycopy(pushBody.get(PUSH_ID_NM).getBytes(PUSH_ENCODING), 0, sendHeader, 4, 12);   //Transaction Id
            System.arraycopy(this.channelID.getBytes(PUSH_ENCODING), 0, sendHeader, 16, 14);             //Channel Id
            System.arraycopy(destIp.getBytes(PUSH_ENCODING), 0, sendHeader, 32, destIp.getBytes(PUSH_ENCODING).length);//Destination IP
            System.arraycopy(Ints.toByteArray(byteDATA.length), 0, sendHeader, 60, 4);                 //Data Length
            System.arraycopy(byteDATA, 0, sendHeader, 64, byteDATA.length);                                   //Data

            log.trace("sendHeader Len =" + sendHeader.length);

            this.getPushOut().write(sendHeader);
            this.getPushOut().flush();

        } catch (ConnectException e) {
            log.debug("[setNoti][ConnectException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-100, e.getClass().getName(), e);
        } catch (java.net.SocketException e) {
            log.debug("[setNoti][SocketException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-200, e.getClass().getName(), e);
        } catch (SocketTimeoutException e) {
            log.debug("[setNoti][SocketTimeoutException][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-300, e.getClass().getName(), e);
        } catch (Exception e) {
            log.debug("[setNoti][Exception][" + e.getClass().getName() + "][" + e.getMessage() + "]");
            failCount++;
            throw new PushBizException(-400, e.getClass().getName(), e);
        }

    }

    public PushRcvHeaderVo recvPushMessageHeader() throws PushBizException {

        try
        {
            log.trace("======================= Header =========================");
            //Header
            int readBits;
            byte[] byteHeader = new byte[PUSH_MSG_HEADER_LEN];

            readBits = this.getPushIn().read(byteHeader, 0, PUSH_MSG_HEADER_LEN);

            if (readBits != PUSH_MSG_HEADER_LEN) {
                failCount++;
                throw new PushBizException(-200, "", new Exception());
            }

            log.trace("[setNoti] 서버 응답 response_MessageID = " + byteToInt(byteHeader));
            log.trace("[setNoti] 서버 응답 Transaction ID = " + getEncodeStr(byteHeader, 4, 12));
            log.trace("[setNoti] 서버 응답 Destination IP = " + getEncodeStr(byteHeader, 16, 16));
            log.trace("[setNoti] 서버 응답 ChannelId = " + getEncodeStr(byteHeader, 32, 14));

            int responseDataLength = byteToInt(byteHeader, PUSH_MSG_HEADER_LEN - 4);
            log.trace("[setNoti] 서버 응답 responseDataLength = " + responseDataLength);

            log.trace("======================= Body =========================");
            byte[] byteBody = new byte[responseDataLength];

            readBits = this.getPushIn().read(byteBody, 0, byteBody.length);

            if (readBits != responseDataLength) {
                failCount++;
                throw new PushBizException(-200, "", new Exception());
            }

            byte[] bResponseCode = new byte[2];
            System.arraycopy(byteBody, 0, bResponseCode, 0, bResponseCode.length);
            String responseCode = new String(bResponseCode, PUSH_ENCODING);
            log.trace("[setNoti] 서버 응답 response Code = " + responseCode);

            return PushRcvHeaderVo.builder().status(responseCode).recvLength(responseDataLength).recvBuffer(byteBody).build();
        } catch (IOException e) {
            throw new PushBizException(-400, e.getClass().getName(), e);
        }
    }

    public PushResponseDto recvPushMessageBody(PushRcvHeaderVo pushRcvHeaderVo) throws PushBizException {

        try
        {
            switch (pushRcvHeaderVo.getStatus()) {
                case "SC" :
                    log.trace("[setNoti]ChannelConnectionRequest 성공");
                    log.trace("[" + this.getChannelID() + "][OPEN_E][] - [SUCCESS]");

                    if (pushRcvHeaderVo.getRecvLength() > 2) {
                        byte[] bJsonMsg = new byte[pushRcvHeaderVo.getRecvLength() - 2];
                        System.arraycopy(pushRcvHeaderVo.getRecvBuffer(), 2, bJsonMsg, 0, bJsonMsg.length);
                        String retJsonMsg = new String(bJsonMsg, PUSH_ENCODING);
                        retJsonMsg = retJsonMsg.replaceAll("(\r\n|\n)", "");

                        JsonNode jsonNodeR = objectMapper.readTree(retJsonMsg);

                        String statusCode = "";
                        String statusMsg = "";

                        if(jsonNodeR != null && jsonNodeR.has(RESPONSE_ID_NM)) {
                            JsonNode jsonNodeStatus = jsonNodeR.get(RESPONSE_ID_NM);
                            statusCode = jsonNodeStatus.has(RESPONSE_STATUS_CD) ? jsonNodeStatus.get(RESPONSE_STATUS_CD).asText() : "";
                            statusMsg = jsonNodeStatus.has(RESPONSE_STATUS_MSG) ? jsonNodeStatus.get(RESPONSE_STATUS_MSG).asText() : "";
                        }

                        socketTime = Instant.now().getEpochSecond();

                        return PushResponseDto.builder().statusCode(statusCode).statusMsg(statusMsg).build();
                    }

                    break;
                case "FA" :
                    log.debug("[setNoti]ChannelConnectionRequest 실패1");

                    if (pushRcvHeaderVo.getRecvLength() >= 4) {
                        byte[] bResponseState = new byte[2];
                        System.arraycopy(pushRcvHeaderVo.getRecvBuffer(), 2, bResponseState, 0, bResponseState.length);
                        short responseState = byteToShort(bResponseState);
                        log.debug("[setNoti] 서버 응답 response Status Code = " + responseState);

                        failCount++;
                        return PushResponseDto.builder().statusCode("FA").statusMsg("" +responseState).build();
                    }

                    break;

                default:
                    failCount++;
                    return PushResponseDto.builder().statusCode("FA").statusMsg("Internal Error").build();
            }
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            failCount++;
            throw new PushBizException(-400, e.getClass().getName(), e);
        }

        failCount++;
        return PushResponseDto.builder().statusCode("FA").statusMsg("Internal Error").build();
    }

    public void closeSocket() {
        log.trace("[===>closeSocket] {}", this);
        try {
            PushSocketInfo socketInfo = this;
            socketInfo.getPushSocket().close();

            if (!socketInfo.getPushSocket().isClosed()) {
                socketInfo.getPushIn().close();
                socketInfo.getPushOut().close();
                socketInfo.getPushSocket().close();
            }
        } catch (Exception e) {
            log.trace("[closeSocket]" + e.getClass().getName());
            log.trace("[closeSocket]" + e.getMessage());
        }

        isOpened = false;
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

    private String getEncodeStr(byte[] src, int offset, int length) {
        byte[] tmpArray = new byte[length];
        System.arraycopy(src, offset, tmpArray, 0, length);
        try {
            return new String(tmpArray, PUSH_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public String toString() {
        long lastTime = this.getSocketTime();

        String time = "";
        if(lastTime > 0 ) {
            time = String.valueOf(Instant.now().getEpochSecond() - lastTime);
        }

        String port = this.pushSocket != null ?  this.pushSocket.getPort() + "" : "";
        return channelID + ":"  + port + ",isOpened:" + isOpened + ",failCount:" + failCount + ",time:" + time;
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
}
