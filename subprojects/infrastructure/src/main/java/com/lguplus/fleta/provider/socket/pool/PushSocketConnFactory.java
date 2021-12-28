package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.exception.push.PushBizException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
public class PushSocketConnFactory extends BasePooledObjectFactory<PushSocketInfo> {

    private final PushServerInfoVo serverInfo;

    private final AtomicInteger commChannelNum = new AtomicInteger(0);

    public PushSocketConnFactory(PushServerInfoVo pushServerInfoVo) {
        this.serverInfo = pushServerInfoVo;
    }

    @Override
    public PushSocketInfo create() {

        PushSocketInfo socketInfo = new PushSocketInfo();

        try {
            socketInfo.openSocket(serverInfo.getHost(), serverInfo.getPort(), serverInfo.getTimeout(), getChannelId(), serverInfo.getDestinationIp());
            log.trace("=== factory create Socket : {}", socketInfo);
        } catch (PushBizException e) {
            e.printStackTrace();
            socketInfo.closeSocket();
            log.debug("=== factory create Socket failure: {}", socketInfo);
            return null;
        }

        return socketInfo;
    }

    @Override
    public boolean validateObject(PooledObject<PushSocketInfo> p) {

        PushSocketInfo socketInfo = p.getObject();

        if(socketInfo == null) {
            return false;
        }

        Socket socket = socketInfo.getPushSocket();

        if(socket == null || socket.getInetAddress() == null || !socket.isConnected()
           || !socketInfo.isOpened() || socketInfo.isFailure())
        {
            return false;
        }

        long connTime = Instant.now().getEpochSecond() - socketInfo.getLastTransactionTime();

        boolean timeover = serverInfo.getCloseSecond() <= (Instant.now().getEpochSecond() - socketInfo.getLastTransactionTime());

        if(timeover && connTime < 300) {
            log.debug("validateObject timeout: close_secs:{} time:{} info:{}", serverInfo.getCloseSecond()
                    , (Instant.now().getEpochSecond() - socketInfo.getLastTransactionTime())
                    , socketInfo);
            socketInfo.isServerInValidStatus();
        }

        return serverInfo.getCloseSecond() > (Instant.now().getEpochSecond() - socketInfo.getLastTransactionTime());
    }

    @Override
    public PooledObject<PushSocketInfo> wrap(PushSocketInfo socketInfo) {
        return new DefaultPooledObject<>(socketInfo);
    }

    @Override
    public void destroyObject(PooledObject<PushSocketInfo> p) {
        PushSocketInfo socketInfo = p.getObject();
        log.trace("=== factory destroy Socket : {}", socketInfo);
        socketInfo.closeSocket();
    }

    private String getChannelId() {
        if(commChannelNum.get() >= 9999) {
            commChannelNum.set(0);
        }

        String hostname;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname = serverInfo.getDefaultChannelHost();
        }

        hostname = hostname.replace("DESKTOP-", "");
        hostname = hostname + hostname;

        String channelHostNm = (hostname + "00000000").substring(0, 6);
        String channelPortNm = (serverInfo.getChannelPort() + "0000").substring(0, 4);

        channelHostNm = "S" +  channelHostNm.substring(1);

        return channelHostNm + channelPortNm + String.format("%04d", commChannelNum.incrementAndGet());
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class PushServerInfoVo {
        private String host;
        private int port;
        private int timeout;
        private int channelPort;
        private String defaultChannelHost;
        private String destinationIp;
        private int closeSecond;
        private boolean isLgPush;
    }


}
