package com.lguplus.fleta.provider.socket.pool;

import com.lguplus.fleta.exception.push.PushBizException;
import lombok.Getter;
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

    private String host;
    private String port;
    private String timeout;
    private String channelPort;
    private String defaultChannelHost;
    private String destinationIp;
    private String closeSecond;
    private boolean isLgPush;

    private AtomicInteger _channelNum = new AtomicInteger(0);

    public PushSocketConnFactory(String host, String port, String timeout, String serverPort, String defChannelHost, String destIp, String closeSecond, boolean isLgPush) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.channelPort = serverPort;
        this.defaultChannelHost = defChannelHost;
        this.destinationIp = destIp;
        this.closeSecond = closeSecond;
        this.isLgPush = isLgPush;
    }

    @Override
    public PushSocketInfo create() {

        PushSocketInfo socketInfo = new PushSocketInfo(new Socket());

        try {
            socketInfo.openSocket(host, Integer.parseInt(port), Integer.parseInt(timeout), getChannelId(), destinationIp);
            log.debug("=== factory create Socket : {}", socketInfo);
        } catch (PushBizException e) {
            e.printStackTrace();
            log.debug("=== factory create Socket failure: {}", socketInfo);
        }

        return socketInfo;
    }

    @Override
    public boolean validateObject(PooledObject<PushSocketInfo> p) {

        PushSocketInfo socketInfo = p.getObject();

        if(socketInfo.getPushSocket() == null) {
            return false;
        }

        if(socketInfo.getPushSocket().getInetAddress() == null) {
            return false;
        }

        //log.debug("=== factory validateObject SocketInfo #1: {}", p.getObject());
        if(!socketInfo.getPushSocket().isConnected())
            return false;

        /*
        if(!socketInfo.isOpened()) {
            //Reconnect
            try {
                socketInfo.openChannel();
            }  catch (PushBizException e) {
                e.printStackTrace();
                log.debug("=== factory Reconnect SocketInfo failure: {}", socketInfo);
            }
        }
        */

        if(!socketInfo.isOpened()) {
            return false;
        }

        if(socketInfo.getFailCount() > 0) {
            return false;
        }

        long lastTime = socketInfo.getSocketTime();
        long currTime = Instant.now().getEpochSecond();

        if(lastTime < 0) {
            return false;
        }

        if(lastTime > 0 && Integer.parseInt(closeSecond) <= (currTime - lastTime)) {
            return false;
        }

        //log.debug("=== factory validateObject SocketInfo #2: {} true", p.getObject());
        return true;
    }

    @Override
    public PooledObject<PushSocketInfo> wrap(PushSocketInfo socketInfo) {
        return new DefaultPooledObject<>(socketInfo);
    }

    @Override
    public void destroyObject(PooledObject<PushSocketInfo> p) throws Exception {
        PushSocketInfo socketInfo = p.getObject();
        log.debug("=== factory destroy Socket : {}", socketInfo);
        socketInfo.closeSocket();
        //log.debug("=== factory destroyObject SocketInfo : {}", socketInfo);
    }

    private String getChannelId() {
        if(_channelNum.get() >= 9999) {
            _channelNum.set(0);
        }

        String hostname = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname = defaultChannelHost;
        }

        hostname = hostname.replace("DESKTOP-", "");
        hostname = hostname + hostname;

        String channelHostNm = (hostname + "00000000").substring(0, 6);
        String channelPortNm = (channelPort + "0000").substring(0, 4);

        String channelId = channelHostNm + channelPortNm + String.format("%04d", _channelNum.incrementAndGet());
        //log.debug("channelId : {}", channelId);

        return channelId;
    }


}
