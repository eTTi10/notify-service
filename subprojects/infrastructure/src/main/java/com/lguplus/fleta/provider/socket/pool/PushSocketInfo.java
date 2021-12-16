package com.lguplus.fleta.provider.socket.pool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class PushSocketInfo {
    private Socket pushSocket;              //Push 소켓
    private InputStream pushIn;                //Push InputStream
    private DataInputStream pushDataIn;        //Push DataInputStream
    private OutputStream pushOut;            //Push OutputStream
    private DataOutputStream pushDataOut;   //Push DataOutputStream

    private String channelID;                //Push Header channelID
    private int channelNum;                    //Push Header channelNum
    private String destIp;

    private final String encoding = "euc-kr";
    private final int PUSH_MSG_HEADER_LEN = 64;

    private long socketTime = -1;
    private boolean isOpened = false;
    //private boolean isSC = true;
    private int failCount = 0;

    public PushSocketInfo(Socket pushSocket) {
        this.pushSocket = pushSocket;
    }

    public void openSocket(final String _host, final int _port, final int _timeout, final String _channelID, final String _destIp) throws PushBizException {
        try {
            SocketAddress adder = new InetSocketAddress(_host, _port);
            this.getPushSocket().connect(adder, _timeout);

            this.setPushIn(this.getPushSocket().getInputStream());
            this.setPushDataIn(new DataInputStream(this.getPushIn()));
            this.setPushOut(this.getPushSocket().getOutputStream());
            this.setPushDataOut(new DataOutputStream(this.getPushOut()));

            this.setChannelID(_channelID);
            this.getPushSocket().setSoTimeout(_timeout);
            this.setDestIp(_destIp);

            byte[] byte_DestinationIP = new byte[16];
            System.arraycopy(destIp.getBytes(encoding), 0, byte_DestinationIP, 0, destIp.getBytes(encoding).length);

            log.trace("[OpenSocket]byteTotalLen=" + PUSH_MSG_HEADER_LEN); //64

            byte[] sendHeader = new byte[PUSH_MSG_HEADER_LEN];
            System.arraycopy(Ints.toByteArray(1), 0, sendHeader, 0, 4);
            System.arraycopy(this.getChannelID().getBytes(encoding), 0, sendHeader, 16, 14);
            System.arraycopy(byte_DestinationIP, 0, sendHeader, 32, 16);
            System.arraycopy(Ints.toByteArray(0), 0, sendHeader, 60, 4);

            this.getPushDataOut().write(sendHeader);
            this.getPushDataOut().flush();

            log.trace("======================= Header =========================");
            //Header
            byte[] byte_header = new byte[PUSH_MSG_HEADER_LEN];
            this.getPushDataIn().read(byte_header, 0, PUSH_MSG_HEADER_LEN);

            log.trace("[OpenSocket] 서버 응답 response_MessageID = " + byteToInt(byte_header));
            log.trace("[OpenSocket] 서버 응답 Destination IP = " + getEncodeStr(byte_header, 16, 16));
            log.trace("[OpenSocket] 서버 응답 ChannelId = " + getEncodeStr(byte_header, 32, 14));

            int response_DataLength = byteToInt(byte_header, PUSH_MSG_HEADER_LEN - 4);
            log.trace("[OpenSocket] 서버 응답 response_DataLength = " + response_DataLength);

            log.trace("======================= Body =========================");
            byte[] byte_body = new byte[response_DataLength];
            this.getPushDataIn().read(byte_body, 0, byte_body.length);

            String responseCode = getEncodeStr(byte_body, 0, 2);
            log.trace("[OpenSocket] 서버 응답 response Code = " + responseCode);

            byte[] bResponseState = new byte[2];
            System.arraycopy(byte_body, 2, bResponseState, 0, bResponseState.length);
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

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode oNode = objectMapper.createObjectNode();
            oNode.set("request", objectMapper.valueToTree(pushBody));
            String jsonStr = oNode.toString();

            byte[] byte_DATA = jsonStr.getBytes(encoding);
            log.trace("[setNoti] json = {}, {}", byte_DATA.length, jsonStr);

            byte[] sendHeader = new byte[PUSH_MSG_HEADER_LEN + byte_DATA.length];
            System.arraycopy(Ints.toByteArray(15), 0, sendHeader, 0, 4);                    //Message Id
            System.arraycopy(pushBody.get("push_id").getBytes(encoding), 0, sendHeader, 4, 12);   //Transaction Id
            System.arraycopy(this.channelID.getBytes(encoding), 0, sendHeader, 16, 14);             //Channel Id
            System.arraycopy(destIp.getBytes(encoding), 0, sendHeader, 32, destIp.getBytes(encoding).length);//Destination IP
            System.arraycopy(Ints.toByteArray(byte_DATA.length), 0, sendHeader, 60, 4);                 //Data Length
            System.arraycopy(byte_DATA, 0, sendHeader, 64, byte_DATA.length);                                   //Data

            log.trace("sendHeader Len =" + sendHeader.length);

            this.getPushDataOut().write(sendHeader);
            this.getPushDataOut().flush();

            log.trace("======================= Header =========================");
            //Header
            byte[] byte_header = new byte[PUSH_MSG_HEADER_LEN];
            this.getPushDataIn().read(byte_header, 0, PUSH_MSG_HEADER_LEN);

            log.trace("[setNoti] 서버 응답 response_MessageID = " + byteToInt(byte_header));
            log.trace("[setNoti] 서버 응답 Transaction ID = " + getEncodeStr(byte_header, 4, 12));
            log.trace("[setNoti] 서버 응답 Destination IP = " + getEncodeStr(byte_header, 16, 16));
            log.trace("[setNoti] 서버 응답 ChannelId = " + getEncodeStr(byte_header, 32, 14));

            int response_DataLength = byteToInt(byte_header, PUSH_MSG_HEADER_LEN - 4);
            log.trace("[setNoti] 서버 응답 response_DataLength = " + response_DataLength);

            log.trace("======================= Body =========================");
            byte[] byte_body = new byte[response_DataLength];
            this.getPushDataIn().read(byte_body, 0, byte_body.length);

            byte[] bResponseCode = new byte[2];
            System.arraycopy(byte_body, 0, bResponseCode, 0, bResponseCode.length);
            String responseCode = new String(bResponseCode, encoding);
            log.trace("[setNoti] 서버 응답 response Code = " + responseCode);

            if ("SC".equals(responseCode)) {
                log.trace("[setNoti]ChannelConnectionRequest 성공");
                log.trace("[" + this.getChannelID() + "][OPEN_E][] - [SUCCESS]");

                if (response_DataLength > 2) {
                    byte[] bJsonMsg = new byte[response_DataLength - 2];
                    System.arraycopy(byte_body, 2, bJsonMsg, 0, bJsonMsg.length);
                    String retJsonMsg = new String(bJsonMsg, encoding);
                    retJsonMsg = retJsonMsg.replaceAll("(\r\n|\n)", "");

                    try {
                        if (Long.parseLong(pushBody.get("push_id")) % 1000 == 0) {
                            log.debug("[setNoti]  서버 응답 JSON {} : {}", pushBody.get("push_id"), retJsonMsg);
                        }
                    }catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    JsonNode jsonNodeR = null;
                    try {
                        jsonNodeR = objectMapper.readTree(retJsonMsg);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    String status_code = "";
                    if(jsonNodeR != null && jsonNodeR.has("response") && jsonNodeR.get("response").has("status_code")) {
                        status_code = jsonNodeR.get("response").get("status_code").asText();
                    }
                    String statusmsg = "";
                    if (jsonNodeR != null && jsonNodeR.get("response").has("statusmsg")) {
                        statusmsg = jsonNodeR.get("response").get("statusmsg").asText();
                    }

                    socketTime = Instant.now().getEpochSecond();
                    //failCount = 0;

                    //log.trace("[setNoti] Read Available Count = {}", this.getPushDataIn().available());
                    return PushResponseDto.builder().statusCode(status_code).statusMsg(statusmsg).build();//new PushAnnounceResponseDto(status_code, statusmsg);
                }
            } else if ("FA".equals(responseCode)) {
                log.debug("[setNoti]ChannelConnectionRequest 실패1");

                if (response_DataLength >= 4) {
                    byte[] bResponseState = new byte[2];
                    System.arraycopy(byte_body, 2, bResponseState, 0, bResponseState.length);
                    short responseState = byteToShort(bResponseState);
                    log.debug("[setNoti] 서버 응답 response Status Code = " + responseState);

                    failCount++;
                    //log.trace("[setNoti] Read Available Count = {}", this.getPushDataIn().available());
                    return PushResponseDto.builder().statusCode("FA").statusMsg("" +responseState).build();//new PushAnnounceResponseDto("FA", "" +responseState);
                }
            } else {
                log.debug("[setNoti]ChannelConnectionRequest 실패2");
                return PushResponseDto.builder().statusCode("FA").statusMsg("Internal Error").build();//new PushAnnounceResponseDto("FA", "Internal Error");
            }

            //log.trace("[setNoti] Read Available Count = {}", this.getPushDataIn().available());

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

        failCount++;

        return PushResponseDto.builder().statusCode("FA").statusMsg("Internal Error").build();//return new PushAnnounceResponseDto("FA", "Internal Error");
    }

    public void closeSocket() {
        log.trace("[===>closeSocket] {}", this);
        try {
            PushSocketInfo socketInfo = this;
            socketInfo.getPushSocket().close();

            if (!socketInfo.getPushSocket().isClosed()) {
                socketInfo.getPushIn().close();
                socketInfo.getPushDataIn().close();

                socketInfo.getPushOut().close();
                socketInfo.getPushDataOut().close();

                socketInfo.getPushSocket().close();
            }
        } catch (Exception e) {
            log.trace("[closeSocket]" + e.getClass().getName());
            log.trace("[closeSocket]" + e.getMessage());
        }

        isOpened = false;
    }


    private short byteToShort(byte src[]) {
        return (short) ((src[0] & 0xff) << 8 | src[1] & 0xff);
    }

    private int byteToInt(byte src[], int... b) {
        int offset = 0;
        if(b.length > 0) {
            offset = b[0];
        }
        return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16 | (src[offset + 2] & 0xff) << 8 | src[offset + 3] & 0xff;
    }

    private String getEncodeStr(byte src[], int offset, int length) {
        byte[] _tmpArray = new byte[length];
        System.arraycopy(src, offset, _tmpArray, 0, length);
        try {
            return new String(_tmpArray, encoding);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public String toString() {
        long lastTime = this.getSocketTime();
        long currTime = Instant.now().getEpochSecond();

        String time = "";
        if(lastTime > 0 ) {
            time = String.valueOf(Instant.now().getEpochSecond() - lastTime);
        }

        //String address = this.pushSocket != null && this.pushSocket.getInetAddress() != null ?  this.pushSocket.getInetAddress().getHostAddress() : "";
        String port = this.pushSocket != null ?  this.pushSocket.getPort() + "" : "";
        return channelID + ":"  + port + ",isOpened:" + isOpened + ",failCount:" + failCount + ",time:" + time;
    }
}
