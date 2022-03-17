package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsGateway {

    private static final String CODE_SUCCESS = "0000";
    private static final String MESSAGE_SUCCESS = "성공";
    private static final String CODE_SYSTEM_ERROR = "1500";
    private static final String MESSAGE_SYSTEM_ERROR = "시스템 장애";

    private static final int BIND = 0;
    private static final int BIND_ACK = 1;
    private static final int DELIVER = 2;
    private static final int DELIVER_ACK = 3;
    private static final int REPORT = 4;
    private static final int REPORT_ACK = 5;
    private static final int LINK_SEND  = 6;
    private static final int LINK_RECV = 7;

    private static final int TIMER_RECONNECT = 0;
    private static final int TIMER_LINK_CHECK = 1;
    private static final int TIMER_LINK_RESULT = 2;
    private static final int TIMER_TIME_OUT = 3;

    private static final int TIME_OUT = 5000;				        // 타임아웃(5초)
    private int RECONNECT_TERM = 1000 * 60 * 3;        // 재접속 시간(3분)
    private static final int TIMEOUT_TERM = 1000 * 3;               // 메세지 전송 후 타임아웃 시간(3초)
    private int LINK_CHECK_TERM = 1000 * 50;           // 링크 체크 주기(50초)
    private static final int LINK_ERROR_TERM = 1000 * 5;            // 링크 에러 체크 시간(5초)

    private boolean isLinked = false;
    private boolean isBind = false; //true이더라도 바인딩 완료된 상태가 아니라 접속만 완료가 된 상태

    private String mIpAddress;
    public String mResult = "";
    private String mID;
    private String mPassword;
    private int mPort;

    private Date mLastSendDate;

    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private Log mFileLog;
    private Log mStatusLog;

    private Socket mSocket;

    private Map<Integer, Timer> mTimerMap = new HashMap<>();


    public SmsGateway(String ip, String port, String id, String password) {

        mTimerMap.put(TIMER_RECONNECT, new Timer());
        mTimerMap.put(TIMER_LINK_CHECK, new Timer());
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        mTimerMap.put(TIMER_TIME_OUT, new Timer());

        String index = StringUtils.defaultIfEmpty(System.getProperty("server.index"), "1");
        mFileLog = LogFactory.getLog("SmsGateway");
        mStatusLog = LogFactory.getLog("SmsStatus");
        mStatusLog.info("SmsGateway" + index);

        mIpAddress = ip;
        mPort = Integer.parseInt(port);
        mID = id;
        mPassword = password;
        mLastSendDate = new Date();

        mStatusLog.info("ip:" + ip);
        mStatusLog.info("port:" + port);

        connectGateway();
    }

    public boolean isBind() {
        return isBind;
    }

    public Date getLastSendDate() {
        return mLastSendDate;
    }

    public int getPort() {
        return mPort;
    }

    public void clearResult() {
        mResult = "";
    }

    public boolean connectGateway() {

        mStatusLog.info("Connect Try[" + mPort + "]");

        mTimerMap.get(TIMER_RECONNECT).cancel();
        mTimerMap.get(TIMER_LINK_CHECK).cancel();
        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.get(TIMER_TIME_OUT).cancel();

        InetSocketAddress socketAddress = new InetSocketAddress(mIpAddress, mPort);

        try {
            if (null != mSocket) {
                mSocket.close();
                mSocket = null;
            }

            mSocket = new Socket();

            mSocket.connect(socketAddress, TIME_OUT);

            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();

            isBind = true;

            mStatusLog.info("Connect Success[" + mPort + "]");
            mStatusLog.info("Socket Open[" + mPort + "]");

            // 게이트웨이에 접속 시도하는 쓰레드
            Thread thread = new Thread(new SmsGatewayTask());
            thread.start();

            bindGateway();
        } catch (IOException e) {
            mStatusLog.error("connectGateway Error");
            //reConnectGateway(); <=========== 연결되지 않는 커넥션...임시주석처리 계속해서 로그가 찍힘
        }

        return  true;
    }

    private void reConnectGateway() {

        mStatusLog.info("ReConnect Try[" + mPort + "]");

        isBind = false;

        mTimerMap.get(TIMER_RECONNECT).cancel();
        mTimerMap.put(TIMER_RECONNECT, new Timer());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectGateway();
            }
        };

        mTimerMap.get(TIMER_RECONNECT).schedule(timerTask, RECONNECT_TERM);
    }

    private void bindGateway() throws IOException {

        byte[] body = new byte[32];
        byte[] idBytes = mID.getBytes();
        byte[] pwdBytes = mPassword.getBytes();

        System.arraycopy(idBytes, 0, body, 0, idBytes.length);
        System.arraycopy(pwdBytes, 0, body, 16, pwdBytes.length);

        byte[] header = new byte[8];
        byte[] msgType = intToByte(BIND);
        byte[] msgLen = intToByte(body.length);

        System.arraycopy(msgType, 0, header, 0, msgType.length);
        System.arraycopy(msgLen, 0, header, 4, msgLen.length);

        mOutputStream.write(header);
        mOutputStream.write(body);

        mStatusLog.info("Bind Try[" + mPort + "]");
    }

    public void sendMessage(String orgAddr, String dstAddr, String callBack, String message, int sn) throws IOException {

        byte[] body = new byte[264];

        byte[] tidBytes = intToByte(4098);
        byte[] orgAddrBytes = orgAddr.getBytes();
        byte[] dstAddrBytes = dstAddr.getBytes();
        byte[] callBackBytes = callBack.getBytes();
        byte[] messageBytes = message.getBytes("KSC5601");
        byte[] snBytes = intToByte(sn);

        System.arraycopy(tidBytes, 0, body, 0, tidBytes.length);
        System.arraycopy(orgAddrBytes, 0, body, 4, orgAddrBytes.length);
        System.arraycopy(dstAddrBytes, 0, body, 36, dstAddrBytes.length);
        System.arraycopy(callBackBytes, 0, body, 68, callBackBytes.length);
        System.arraycopy(messageBytes, 0, body, 100, messageBytes.length);
        System.arraycopy(snBytes, 0, body, 260, snBytes.length);

        byte[] header = new byte[8];
        byte[] msgType = intToByte(DELIVER);
        byte[] msgLen = intToByte(body.length);

        System.arraycopy(msgType, 0, header, 0, msgType.length);
        System.arraycopy(msgLen, 0, header, 4, msgLen.length);

        mOutputStream.write(header);
        mOutputStream.write(body);

        mLastSendDate = new Date();

        mTimerMap.get(TIMER_TIME_OUT).cancel();
        mTimerMap.put(TIMER_TIME_OUT, new Timer());
        TimerTask timerTask = new ErrorTimerTask(this);

        mTimerMap.get(TIMER_TIME_OUT).schedule(timerTask, TIMEOUT_TERM);
        //3초후에 mResult가 빈 값인지 체크하여 1500 처리
    }

    public boolean checkLink() throws IOException {

        mStatusLog.info("checkLink[" + mPort + "]");

        byte[] header = new byte[8];

        System.arraycopy(intToByte(LINK_SEND), 0, header, 0, 4);
        System.arraycopy(intToByte(0), 0, header, 4, 4);

        mOutputStream.write(header);

        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        TimerTask timerTask = new LinkTimerTask(this);

        mTimerMap.get(TIMER_LINK_RESULT).schedule(timerTask, LINK_ERROR_TERM);

        return true;
    }

    private void sendReport() throws IOException {

        byte[] body = intToByte(0);

        byte[] header = new byte[8];
        byte[] msgType = intToByte(REPORT_ACK);
        byte[] msgLen = intToByte(body.length);

        System.arraycopy(msgType, 0, header, 0, msgType.length);
        System.arraycopy(msgLen, 0, header, 4, msgLen.length);

        mOutputStream.write(header);
        mOutputStream.write(body);
    }

    private byte[] intToByte(int value) {

        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(value);
        buff.order(ByteOrder.BIG_ENDIAN);

        return buff.array();
    }

    private int readBufferToInt(int bufferSize) throws IOException {

        byte[] buffer = new byte[bufferSize];

        int length = mInputStream.read(buffer);

        if (0 < length) {
            ByteBuffer rHeader = ByteBuffer.wrap(buffer, 0, bufferSize);
            return rHeader.getInt();
        } else {
            return -1;
        }
    }

    private String readBufferToString(int bufferSize) throws IOException {

        byte[] buffer = new byte[bufferSize];
        int length = mInputStream.read(buffer);

        if (0 < length) {
            return new String(buffer, 0, bufferSize).trim();
        } else {
            return "";
        }
    }

    @Async
    public Future<SmsGatewayResponseDto> getResult() {

        SmsGatewayResponseDto smsGatewayResponseDto = null;

        while (mResult.isEmpty()) {
            LockSupport.parkNanos(10 * 1000000);
        }

        log.debug("mResult:{}", mResult);

        if (mResult.equals(CODE_SUCCESS)) {  // 0000
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(mResult)
                    .message(MESSAGE_SUCCESS)
                    .build();
        } else if (mResult.equals(CODE_SYSTEM_ERROR)) {   // 1500
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(mResult)
                    .message(MESSAGE_SYSTEM_ERROR)
                    .build();
        }

        /* 서버 연동 안될 때 이 곳에 성공 smsGatewayResponseDto 강제 리턴 */

        clearResult();
        mTimerMap.get(TIMER_TIME_OUT).cancel();
        return new AsyncResult<SmsGatewayResponseDto>(smsGatewayResponseDto);
    }

    //소켓서버의 응답을 파싱한다
    private void readHeader() throws IOException {
        int type = readBufferToInt(4);
        int result;

        String orgAddr;
        String dstAddr;
        int sn;

        switch (type) {
            case BIND_ACK:

                result = readBufferToInt(4);

                mStatusLog.info("readHeader() BIND_ACK result:"+result);

                isBind = 0 == result;

                if (isBind) {
                    mTimerMap.get(TIMER_LINK_CHECK).cancel();
                    mTimerMap.put(TIMER_LINK_CHECK, new Timer());

                    TimerTask timerTask = new BindTimerTask(this);
//                    timerTask.run();

                    mTimerMap.get(TIMER_LINK_CHECK).schedule(timerTask, LINK_CHECK_TERM, LINK_CHECK_TERM);

                    mStatusLog.info("Bind Success[" + mPort + "]");
                } else {
                    mStatusLog.info("Bind Fail[" + mPort + "]");
                    reConnectGateway();
                }
                break;
            case DELIVER_ACK:
                result = readBufferToInt(4);

                mStatusLog.info("readHeader() DELIVER_ACK result:"+result);

                switch (result) {
                    case 0:
                        mResult = CODE_SUCCESS;  // 0000
                        break;
                    case 1:
                        mResult = CODE_SYSTEM_ERROR; // 1500
                        break;
                    default:
                        break;
                }

                break;
            case REPORT:
                result = readBufferToInt(4);
                orgAddr = readBufferToString(32);
                dstAddr = readBufferToString(32);
                sn = readBufferToInt(4);
                String time = readBufferToString(20);
                String code = readBufferToString(12);

                StringBuilder resultBuilder = new StringBuilder();

                resultBuilder.append(result).append("|");
                resultBuilder.append(mPort).append("|");
                resultBuilder.append(orgAddr).append("|");
                resultBuilder.append(dstAddr).append("|");
                resultBuilder.append(sn).append("|");
                resultBuilder.append(code).append("|");
                resultBuilder.append(time);

                mFileLog.info(resultBuilder.toString());

                sendReport();
                break;
            case LINK_RECV:
                mStatusLog.info("Link Success[" + mPort + "]");
                isLinked = true;
                break;
            default:
                break;
        }

    }

    //게이트웨이에 접속성공 할 때까지 게이트웨이 접속을 무제한으로 시도함
    private class SmsGatewayTask implements Runnable {
        @Override
        public void run() {
            while (isBind) {
                try {
                    readHeader();
                } catch (IOException ignored) {
                    mStatusLog.error("readHeader Error");
                    reConnectGateway();
                }
            }
        }
    }

    static public class BindTimerTask extends TimerTask {
        private SmsGateway smsGateway;

        public BindTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            try {
                smsGateway.checkLink();
            } catch (IOException e) {
                log.error("BIND_ACK Error");
                //smsGateway.mStatusLog.error("BIND_ACK Error");
            }
        }
    }

    static public class LinkTimerTask extends TimerTask {
        private SmsGateway smsGateway;

        public LinkTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            if (smsGateway.isLinked) {
                smsGateway.isLinked = false;
            } else {
                log.info("Link Fail[" + smsGateway.mPort + "]");
                smsGateway.isBind = false;
                smsGateway.connectGateway();
            }
        }
    }

    static public class ErrorTimerTask extends TimerTask {
        private SmsGateway smsGateway;

        public ErrorTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            if (smsGateway.mResult.isEmpty()) {
                log.debug("mResult.isEmpty() then 1500");
                smsGateway.mResult = CODE_SYSTEM_ERROR;
            }
        }
    }

}
