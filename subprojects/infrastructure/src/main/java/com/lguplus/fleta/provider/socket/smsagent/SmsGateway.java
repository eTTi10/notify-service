package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.SneakyThrows;
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
    private static final int TIMER_LINK_CHECK = 1;
    private static final int TIMER_LINK_RESULT = 2;
    private static final int TIMER_TIME_OUT = 3;

    private static final int TIME_OUT = 5000;                        // 타임아웃(5초)
    private static final int RECONNECT_TERM = Integer.sum(1000 * 6 * 3, 0);        // 재접속 시간(3분)
    private static final int TIMEOUT_TERM = 1000 * 3;               // 메세지 전송 후 타임아웃 시간(3초)
    private static final int LINK_CHECK_TERM = Integer.sum(1000 * 5, 0);           // 링크 체크 주기(50초)
    private static final int LINK_ERROR_TERM = 1000 * 5;            // 링크 에러 체크 시간(5초)
    private final String mIpAddress;
    private final String mID;
    private final String mPassword;
    private final int mPort;
    private final Map<Integer, Timer> mTimerMap = new HashMap<>();
    private boolean isLinked = false;
    private boolean isBind = false; //true 이더라도 바인딩 완료된 상태가 아니라 접속만 완료가 된 상태
    private String mResult = "";
    private Date mLastSendDate;
    private DataInputStream mInputStream;
    private DataOutputStream mOutputStream;
    private Socket mSocket;

    private final Thread connectThread;
    private final Thread readThread;
    private final Thread linkThread;

    private final ExecutorService connectThreadExecutorService;

    private final ExecutorService readThreadExecutorService;

    private final ScheduledExecutorService linkThreadExecutorService;

    public SmsGateway(String ip, String port, String id, String password) {
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

        connectThread = new Thread(new ConnectTask());
        readThread = new Thread(new SocketReadTask());
        linkThread = new Thread(new SocketReadTask());

        connectThreadExecutorService = Executors.newSingleThreadExecutor();
        readThreadExecutorService = Executors.newSingleThreadExecutor();
        linkThreadExecutorService = Executors.newSingleThreadScheduledExecutor();
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

        mTimerMap.get(TIMER_LINK_CHECK).cancel();
        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.get(TIMER_TIME_OUT).cancel();

        while (mSocket == null) {
            connectThreadExecutorService.submit(connectThread);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        }

        shutdownExecutorService(connectThreadExecutorService);

        log.debug("Success Connection");

        try {
            bindGateway();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void bindGateway() throws IOException {

        byte[] body = new byte[32];
        byte[] idBytes = mID.getBytes();
        byte[] pwdBytes = mPassword.getBytes();

        System.arraycopy(idBytes, 0, body, 0, idBytes.length);
        System.arraycopy(pwdBytes, 0, body, 16, pwdBytes.length);

        byte[] header = getHeader(intToByte(BIND), intToByte(body.length));

        mOutputStream.write(header);
        mOutputStream.write(body);

        readThreadExecutorService.submit(readThread);
        log.info("Bind Try[" + mPort + "]");

    }

    private byte[] getHeader(byte[] msgType, byte[] msgLen) {
        byte[] header = new byte[8];
        System.arraycopy(msgType, 0, header, 0, msgType.length);
        System.arraycopy(msgLen, 0, header, 4, msgLen.length);
        return header;
    }

    public void sendMessage(String orgAddr, String dstAddr, String callBack, String message, int sn) throws IOException {

        byte[] body = new byte[264];

        byte[] tidBytes = intToByte(4098);
        byte[] orgAddressBytes = orgAddr.getBytes();
        byte[] dstAddressBytes = dstAddr.getBytes();
        byte[] callBackBytes = callBack.getBytes();
        byte[] messageBytes = message.getBytes("KSC5601");
        byte[] snBytes = intToByte(sn);

        System.arraycopy(tidBytes, 0, body, 0, tidBytes.length);
        System.arraycopy(orgAddressBytes, 0, body, 4, orgAddressBytes.length);
        System.arraycopy(dstAddressBytes, 0, body, 36, dstAddressBytes.length);
        System.arraycopy(callBackBytes, 0, body, 68, callBackBytes.length);
        System.arraycopy(messageBytes, 0, body, 100, messageBytes.length);
        System.arraycopy(snBytes, 0, body, 260, snBytes.length);

        byte[] header = getHeader(intToByte(DELIVER), intToByte(body.length));

        mOutputStream.write(header);
        mOutputStream.write(body);

        mLastSendDate = new Date();

        mTimerMap.get(TIMER_TIME_OUT).cancel();
        mTimerMap.put(TIMER_TIME_OUT, new Timer());

        TimerTask timerTask = new ErrorTimerTask(this);

        mTimerMap.get(TIMER_TIME_OUT).schedule(timerTask, TIMEOUT_TERM);
        //3초후에 mResult 가 빈 값인지 체크하여 1500 처리
    }

    public void checkLink() throws IOException {

        log.info("checkLink[" + mPort + "]");

        byte[] header = getHeader(intToByte(LINK_SEND), intToByte(0));
        mOutputStream.write(header);

        readThreadExecutorService.submit(readThread);

    }

    private void sendReport() throws IOException {

        byte[] body = intToByte(0);

        byte[] header = getHeader(intToByte(REPORT_ACK), intToByte(body.length));

        mOutputStream.write(header);
        mOutputStream.write(body);
    }

    private byte[] intToByte(int value) {

        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(value);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        return buff.array();
    }

    private int readBufferToInt() throws IOException {
        byte[] buffer = new byte[4];
        mInputStream.readFully(buffer);

        ByteBuffer rHeader = ByteBuffer.wrap(buffer, 0, 4);
        return rHeader.getInt();

    }

    private String readBufferToString(int bufferSize) throws IOException {

        byte[] buffer = new byte[bufferSize];
        mInputStream.readFully(buffer);

        return new String(buffer, 0, bufferSize).trim();

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
        log.debug("Before read");
        int type = readBufferToInt();
        log.debug("After read");
        // message length
        readBufferToInt();
        int result;

        String orgAddress;
        String dstAddress;
        int sn;

        switch (type) {
            case BIND_ACK:
                result = readBufferToInt();
                String prefix = readBufferToString(16);

                log.debug("prefix {}", prefix);

                log.info("readHeader() BIND_ACK result:" + result);

                setBindState(0 == result);

                if (isBind) {
                    mTimerMap.get(TIMER_LINK_CHECK).cancel();
                    mTimerMap.put(TIMER_LINK_CHECK, new Timer());


                    Runnable linkCheck = new BindTimerTask(this);
                    linkThreadExecutorService.scheduleAtFixedRate(linkCheck , 0, LINK_CHECK_TERM,TimeUnit.SECONDS);

                    log.info("Bind Success[" + mPort + "]");
                } else {
                    log.info("Bind Fail[" + mPort + "]");
                }
                break;
            case DELIVER_ACK:
                result = readBufferToInt();
                orgAddress = readBufferToString(32);
                dstAddress = readBufferToString(32);
                sn = readBufferToInt();

                log.debug("DELIVER_ACK {} {} {}", orgAddress, dstAddress, sn);

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
                result = readBufferToInt();
                orgAddress = readBufferToString(32);
                dstAddress = readBufferToString(32);
                sn = readBufferToInt();
                String time = readBufferToString(20);
                String code = readBufferToString(12);
                log.debug("REPORT");

                String resultBuilder = result + "|"
                    + mPort + "|"
                    + orgAddress + "|"
                    + dstAddress + "|"
                    + sn + "|"
                    + code + "|"
                    + time;

                log.debug(resultBuilder);

                sendReport();
                break;
            case LINK_RECV:
                log.info("Link Success[" + mPort + "]");
                setLinkState(true);
                break;
            default:
                log.debug("default! type : {}", type);
                break;
        }

    }

    public static class BindTimerTask implements Runnable {

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

    public static class LinkTimerTask implements Runnable {

        private final SmsGateway smsGateway;

        public LinkTimerTask(SmsGateway gw) {
            smsGateway = gw;
        }

        @Override
        public void run() {
            if (smsGateway.getLinkState()) {
                smsGateway.setLinkState(false);
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
    private class SocketReadTask implements Runnable {

        @Override
        public void run() {
            try {
                readHeader();
            } catch (IOException exception) {
                log.error("readHeader Error : {}", exception.getMessage());
                setBindState(false);
                connectGateway();
            }
        }
    }

    private class ConnectTask implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            try {
                log.debug("Try connect");
                InetSocketAddress socketAddress = new InetSocketAddress(mIpAddress, mPort);
                if (mSocket != null) {
                    mSocket.close();
                    mSocket = null;
                }
                mSocket = new Socket();
                mSocket.connect(socketAddress, TIME_OUT);

                mInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
                mOutputStream = new DataOutputStream(mSocket.getOutputStream());
            } catch (IOException exception) {
                log.error("connectGateway Error");
                mSocket.close();
                mSocket = null;
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RECONNECT_TERM));
            }
        }
    }
}
