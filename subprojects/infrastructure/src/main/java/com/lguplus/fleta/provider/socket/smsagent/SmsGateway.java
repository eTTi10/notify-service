package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

@Slf4j
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
    private static final int LINK_SEND = 6;
    private static final int LINK_RECV = 7;

    private static final int TIMER_RECONNECT = 0;
    private static final int TIMER_LINK_CHECK = 1;
    private static final int TIMER_LINK_RESULT = 2;
    private static final int TIMER_TIME_OUT = 3;

    private static final int TIME_OUT = 5000;                        // 타임아웃(5초)
    private static final int RECONNECT_TERM = Integer.sum(1000 * 60 * 3, 0);        // 재접속 시간(3분)
    private static final int TIMEOUT_TERM = 1000 * 3;               // 메세지 전송 후 타임아웃 시간(3초)
    private static final int LINK_CHECK_TERM = Integer.sum(1000 * 50, 0);           // 링크 체크 주기(50초)
    private static final int LINK_ERROR_TERM = 1000 * 5;            // 링크 에러 체크 시간(5초)
    private final String mIpAddress;
    private final String mID;
    private final String mPassword;
    private final int mPort;
    private final Map<Integer, Timer> mTimerMap = new HashMap<>();
    private boolean isLinked = false;
    private boolean isBind = false; //true이더라도 바인딩 완료된 상태가 아니라 접속만 완료가 된 상태
    private String mResult = "";
    private Date mLastSendDate;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Socket mSocket;

    public SmsGateway(String ip, String port, String id, String password) {
        mTimerMap.put(TIMER_RECONNECT, new Timer());
        mTimerMap.put(TIMER_LINK_CHECK, new Timer());
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        mTimerMap.put(TIMER_TIME_OUT, new Timer());

        mIpAddress = ip;
        mPort = Integer.parseInt(port);
        mID = id;
        mPassword = password;
        mLastSendDate = new Date();

        log.info("ip:" + ip);
        log.info("port:" + port);

        connectGateway();

    }

    public boolean getBindState() {
        return isBind;
    }

    public synchronized void setBindState(boolean bind) {
        this.isBind = bind;
    }

    public boolean getLinkState() {
        return isLinked;
    }

    public synchronized void setLinkState(boolean link) {
        this.isLinked = link;
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

    public void connectGateway() {

        log.info("Connect Try[" + mPort + "]");

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

            setBindState(true);

            log.info("Connect Success[" + mPort + "]");
            log.info("Socket Open[" + mPort + "]");

            // 게이트웨이에 접속 시도하는 쓰레드
            Thread thread = new Thread(new SmsGatewayTask());
            thread.start();

            bindGateway();
        } catch (IOException e) {
            log.error("connectGateway Error");
            reConnectGateway(); // <=========== 연결되지 않는 커넥션...임시주석처리 계속해서 로그가 찍힘
        }

    }

    private void reConnectGateway() {

        log.info("ReConnect Try[" + mPort + "]");

        setBindState(false);
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

        log.info("Bind Try[" + mPort + "]");
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

    public void checkLink() throws IOException {

        log.info("checkLink[" + mPort + "]");

        byte[] header = new byte[8];

        System.arraycopy(intToByte(LINK_SEND), 0, header, 0, 4);
        System.arraycopy(intToByte(0), 0, header, 4, 4);

        mOutputStream.write(header);

        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        TimerTask timerTask = new LinkTimerTask(this);

        mTimerMap.get(TIMER_LINK_RESULT).schedule(timerTask, LINK_ERROR_TERM);

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
            log.debug("return -1");
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
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
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
        return new AsyncResult<>(smsGatewayResponseDto);
    }

    //소켓서버의 응답을 파싱한다
    private void readHeader() throws IOException {
        int type = readBufferToInt(4);
        // message length
        readBufferToInt(4);
        int result;

        String orgAddr;
        String dstAddr;
        int sn;

        switch (type) {
            case BIND_ACK:

                result = readBufferToInt(4);

                log.info("readHeader() BIND_ACK result:" + result);

                setBindState(0 == result);

                if (isBind) {
                    mTimerMap.get(TIMER_LINK_CHECK).cancel();
                    mTimerMap.put(TIMER_LINK_CHECK, new Timer());

                    TimerTask timerTask = new BindTimerTask(this);

                    mTimerMap.get(TIMER_LINK_CHECK).schedule(timerTask, LINK_CHECK_TERM, LINK_CHECK_TERM);

                    log.info("Bind Success[" + mPort + "]");
                } else {
                    log.info("Bind Fail[" + mPort + "]");
                    reConnectGateway();
                }
                break;
            case DELIVER_ACK:
                result = readBufferToInt(4);

                log.info("readHeader() DELIVER_ACK result:" + result);

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
                log.debug("REPORT");

                StringBuilder resultBuilder = new StringBuilder();

                resultBuilder.append(result).append("|");
                resultBuilder.append(mPort).append("|");
                resultBuilder.append(orgAddr).append("|");
                resultBuilder.append(dstAddr).append("|");
                resultBuilder.append(sn).append("|");
                resultBuilder.append(code).append("|");
                resultBuilder.append(time);

                log.debug(resultBuilder.toString());

                sendReport();
                break;
            case LINK_RECV:
                log.info("Link Success[" + mPort + "]");
                isLinked = true;
                break;
            default:
                log.debug("default! type : {}" , type);
                break;
        }

    }

    public static class BindTimerTask extends TimerTask {

        private final SmsGateway smsGateway;

        public BindTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            try {
                smsGateway.checkLink();
            } catch (IOException e) {
                log.error("BIND_ACK Error");
            }
        }
    }

    public static class LinkTimerTask extends TimerTask {

        private final SmsGateway smsGateway;

        public LinkTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            if (smsGateway.isLinked) {
                smsGateway.isLinked = false;
            } else {
                log.info("Link Fail[" + smsGateway.mPort + "]");
                smsGateway.setBindState(false);
                smsGateway.connectGateway();
            }
        }
    }

    public static class ErrorTimerTask extends TimerTask {

        private final SmsGateway smsGateway;

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

    //게이트웨이에 접속성공 할 때까지 게이트웨이 접속을 무제한으로 시도함
    private class SmsGatewayTask implements Runnable {

        @Override
        public void run() {
            try {
                while (isBind) {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                    log.debug("loop ....");
                    readHeader();
                }
            } catch (IOException ignored) {
                log.error("readHeader Error");
                setBindState(false);
                connectGateway();
            }
        }
    }

}
