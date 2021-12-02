package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.SmsGatewayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsGatewayDomainSocketClient implements SmsGatewayClient {

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
    private static final int RECONNECT_TERM = 1000 * 60 * 3;        // 재접속 시간(3분)
    private static final int TIMEOUT_TERM = 1000 * 3;               // 메세지 전송 후 타임아웃 시간(3초)
    private static final int LINK_CHECK_TERM = 1000 * 50;           // 링크 체크 주기(50초)
    private static final int LINK_ERROR_TERM = 1000 * 5;            // 링크 에러 체크 시간(5초)

    private boolean isLinked = false;
    private boolean isBind = false;

    private String mIpAddress;
    private String mResult = "";
    private String mID;
    private String mPassword;
    private int mPort;

    private Date mLastSendDate;

    private InputStream mInputStream;
    private OutputStream mOutputStream;

    private Log mFileLog;
    private Log mStatusLog;

    private Socket mSoket;

    private Map<Integer, Timer> mTimerMap = new HashMap<Integer, Timer>();


    public SmsGatewayDomainSocketClient(String ip, String port, String id, String password) {

/*
        mTimerMap.put(TIMER_RECONNECT, new Timer());
        mTimerMap.put(TIMER_LINK_CHECK, new Timer());
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        mTimerMap.put(TIMER_TIME_OUT, new Timer());

        String index = StringUtils.defaultIfEmpty(CommonUtil.getSystemProperty("server.index"), "1");
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
        mStatusLog.info("id:" + id);
        mStatusLog.info("password:" + password);

        connectGateway();
*/
    }

    public void sendMessage(String orgAddr, String dstAddr, String callBack, String message, int sn) {

    }

    /*
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

    private void connectGateway() {
        mStatusLog.info("Connect Try[" + mPort + "]");

        mTimerMap.get(TIMER_RECONNECT).cancel();
        mTimerMap.get(TIMER_LINK_CHECK).cancel();
        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.get(TIMER_TIME_OUT).cancel();

        InetSocketAddress socketAddress = new InetSocketAddress(mIpAddress, mPort);
        try {
            if (null != mSoket) {
                mSoket.close();
                mSoket = null;
            }

            mSoket = new Socket();

            mSoket.connect(socketAddress, TIME_OUT);

            mInputStream = mSoket.getInputStream();
            mOutputStream = mSoket.getOutputStream();

            isBind = true;

            mStatusLog.info("Connect Success[" + mPort + "]");
            mStatusLog.info("Socket Open[" + mPort + "]");

            Thread thread = new Thread(new SmsGatewayTask());
            thread.start();

            bindGateway();
        } catch (IOException e) {
            mStatusLog.error("connectGateway Error");
            reConnectGateway();
        }
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
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mResult.isEmpty()) {
                    mResult = Properties.getProperty("flag.system_error");
                }
            }
        };

        mTimerMap.get(TIMER_TIME_OUT).schedule(timerTask, TIMEOUT_TERM);
    }

    private void checkLink() throws IOException {
        mStatusLog.info("checkLink[" + mPort + "]");

        byte[] header = new byte[8];

        System.arraycopy(intToByte(LINK_SEND), 0, header, 0, 4);
        System.arraycopy(intToByte(0), 0, header, 4, 4);

        mOutputStream.write(header);

        mTimerMap.get(TIMER_LINK_RESULT).cancel();
        mTimerMap.put(TIMER_LINK_RESULT, new Timer());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isLinked) {
                    isLinked = false;
                } else {
                    mStatusLog.info("Link Fail[" + mPort + "]");
                    isBind = false;
                    connectGateway();
                }
            }
        };

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
    public Future<ResultVO> getResult() {
        ResultVO resultVO = new ResultVO();

        while (mResult.isEmpty()) {
            try {
                Thread.sleep(10);

            } catch (InterruptedException ignored) {

                mStatusLog.error("getResult Error");
            }

            if (Properties.getProperty("flag.success").equals(mResult)) {

                resultVO.setFlag(mResult);
                resultVO.setMessage(Properties.getProperty("message.success"));

            } else if (Properties.getProperty("flag.system_error").equals(mResult)) {

                resultVO.setFlag(mResult);
                resultVO.setMessage(Properties.getProperty("message.system_error"));
            }
        }

        clearResult();

        mTimerMap.get(TIMER_TIME_OUT).cancel();

        return new AsyncResult<ResultVO>(resultVO);
    }

    private void readHeader() throws IOException {

        int type = readBufferToInt(4);
        int len = readBufferToInt(4);
        int result;

        String orgAddr;
        String dstAddr;
        int sn;

        switch (type) {
            case BIND_ACK:
                result = readBufferToInt(4);
                String prefix = readBufferToString(16);

                isBind = 0 == result;

                if (isBind) {
                    mTimerMap.get(TIMER_LINK_CHECK).cancel();
                    mTimerMap.put(TIMER_LINK_CHECK, new Timer());
                    TimerTask timerTask = new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                checkLink();
                            } catch (IOException ignored) {
                                mStatusLog.error("BIND_ACK Error");
                            }
                        }
                    };

                    mTimerMap.get(TIMER_LINK_CHECK).schedule(timerTask, LINK_CHECK_TERM, LINK_CHECK_TERM);

                    mStatusLog.info("Bind Success[" + mPort + "]");
                } else {
                    mStatusLog.info("Bind Fail[" + mPort + "]");
                    reConnectGateway();
                }
                break;
            case DELIVER_ACK:
                result = readBufferToInt(4);
                orgAddr = readBufferToString(32);
                dstAddr = readBufferToString(32);
                sn = readBufferToInt(4);

                switch (result) {
                    case 0:
                        mResult = Properties.getProperty("flag.success");
                        break;
                    case 1:
                        mResult = Properties.getProperty("flag.system_error");
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
*/


}
