package com.lguplus.fleta.provider.socket.pool;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
public class PushSocketConnFactory extends BasePooledObjectFactory<PushSocketInfo> {

    private final PushServerInfoVo serverInfoVo;

    private final AtomicInteger commChannelNum = new AtomicInteger(0);
    private static final int CHANNEL_MAX_SEQ_NO = 10000;

    public PushSocketConnFactory(PushServerInfoVo pushServerInfoVo) {
        this.serverInfoVo = pushServerInfoVo;
    }

    @Override
    public PushSocketInfo create() throws IOException {

        PushSocketInfo socketInfo = createNewSocketInfo();

        if(socketInfo.isInValid()) {
            socketInfo.closeSocket();
            log.error("=== factory create Socket failure: {}", socketInfo);
            return null;
        }
        else {
            log.trace("=== factory create Socket : {}", socketInfo);
            return socketInfo;
        }
    }

    public PushSocketInfo createNewSocketInfo() throws IOException {

        PushSocketInfo socketInfo = new PushSocketInfo();
        socketInfo.openSocket(serverInfoVo.getHost(), serverInfoVo.getPort(), serverInfoVo.getTimeout(), getChannelId(), serverInfoVo.getDestinationIp());

        return socketInfo;
    }

    @Override
    public boolean validateObject(PooledObject<PushSocketInfo> p) {

        PushSocketInfo socketInfo = p.getObject();

        if(socketInfo.isInValid()) {
            return false;
        }

        if(socketInfo.isTimeoutStatus(serverInfoVo.getCloseSecond()) && socketInfo.getLastUsedSeconds() < 300) {
            socketInfo.isServerInValidStatus();
        }

        return !socketInfo.isTimeoutStatus(serverInfoVo.getCloseSecond());
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

    private String getChannelId() throws IOException {

        InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();

        hostname = hostname.replace("DESKTOP-", "");
        hostname = hostname + hostname;

        String channelHostNm = (hostname + "00000000").substring(0, 6);
        String channelPortNm = (serverInfoVo.getChannelPort() + "0000").substring(0, 4);

        channelHostNm = "S" +  channelHostNm.substring(1);

        return channelHostNm + channelPortNm + String.format("%04d", commChannelNum.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0));
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
