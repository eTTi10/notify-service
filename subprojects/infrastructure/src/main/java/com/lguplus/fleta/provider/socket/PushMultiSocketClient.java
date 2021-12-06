package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;
import com.lguplus.fleta.provider.socket.multi.NettyDecoderTobe;
import com.lguplus.fleta.provider.socket.multi.NettyEncoderTobe;
import com.lguplus.fleta.provider.socket.multi.NettyHandlerTobe;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Push 단건 Socket Client
 * <p>
 * 단건 푸시등록
 */
@Slf4j
@ToString
@RequiredArgsConstructor
@Component
@EnableScheduling
public class PushMultiSocketClient implements PushMultiClient {

    //HDTV
    @Value("${push-comm.push.server.ip}")
    private String serverIp;
    @Value("${push-comm.push.server.port}")
    private String serverPort;
    private int iServerPort;
    @Value("${push-comm.push.socket.timeout}")
    private String socketTimeout;
    private int iSocketTimeout;
    @Value("${push-comm.push.call.retryCnt}")
    private String callRetryCnt;
    private int iCallRetryCnt;

    private AtomicInteger _transactionNo = new AtomicInteger(0);

    private final int CONN_TIMEOUT = 1000;
    private final String ATTACHED_DATA_ID = "MessageInfo.state";

    /* Message */
    public final int HEADER_SIZE = 64;
    public final String SUCCESS = "SC";
    public final String FAIL = "FA";
    public final int CHANNEL_CONNECTION_REQUEST = 1;
    public final int CHANNEL_CONNECTION_REQUEST_ACK = 2;
    public final int CHANNEL_RELEASE_REQUEST = 5;
    public final int CHANNEL_RELEASE_REQUEST_ACK = 6;
    public final int PROCESS_STATE_REQUEST = 13;
    public final int PROCESS_STATE_REQUEST_ACK = 14;
    public final int COMMAND_REQUEST = 15;
    public final int COMMAND_REQUEST_ACK = 16;

    //Netty
    EventLoopGroup eventLoopGroup;
    Bootstrap bootstrap;
    Channel channel;

    //private ConcurrentHashMap<String, MessageInfo> processInfoMap = new ConcurrentHashMap<String, MessageInfo>();

    /**
     * Push Multi 푸시
     *
     * @param paramMap Push Multi 푸시 정보
     * @return Push Multi 푸시 결과
     */
    @Override
    public PushSingleResponseDto requestPushMulti(Map<String, String> paramMap) {
        return null;
    }

    @PostConstruct
    public void initialize() {

        iServerPort = Integer.parseInt(serverPort);
        iSocketTimeout = Integer.parseInt(socketTimeout);
        iCallRetryCnt = Integer.parseInt(callRetryCnt);

        this.eventLoopGroup =  new NioEventLoopGroup();

        log.info("[PushMultiSocketClient] Server IP : " + serverIp + ", port : " + serverPort);
        bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true	)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, iSocketTimeout)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("clientDecoder", new NettyDecoderTobe());
                        p.addLast("clientEncoder", new NettyEncoderTobe());
                        p.addLast("handler", new NettyHandlerTobe());
                    }
                });

        connect();
    }

    /*
    @Scheduled(fixedDelay = 1000 * 20)
    public void multiClientSch() {

    }
    */

    @PreDestroy
    public void destroy() {
        disconnect();
    }

    private void connect() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, iServerPort);
        ChannelFuture connFeature = bootstrap.connect(inetSocketAddress);
        this.channel = connFeature.awaitUninterruptibly().channel();
        log.info("[PushMultiSocketClient] The new channel has been connected. [" + channel.id() + "]");
    }

    private void disconnect() {
        if(channel == null)
            return;

        try {
            if (channel.isActive()) {
                channel.disconnect();
                channel.close();
            }
            log.info("[PushMultiSocketClient] The current channel has been disconnected. [" + channel.id() + "]");
        } catch (Exception ex) {
            log.error("[PushMultiSocketClient] connection closing : {}", ex);
        }
    }

    private boolean isValid() {
        return !(channel == null || channel.isActive() == false || channel.isOpen() == false);
    }

    private boolean write(Object message) {
        try {
            if (null != message && this.channel.isActive()) {
                ChannelFuture wf = this.channel.write(message);
                //this.channel.writeAndFlush(data);
                wf.awaitUninterruptibly(CONN_TIMEOUT);

                //wf.isSuccess()

                if (!wf.isSuccess()) {
                    log.error("[NettyClient] write to server failed");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("[NettyClient] got a exception : {}" + e);
            return false;
        }

        //DefaultChannelGroupFuture
        //DefaultChannelGroupFuture a = null;
        //DefaultChannelGroup b = null;

        return true;
    }

    private Object writeSync(Object message) {
        Object response = null;

        try {
            if (null != message) {

                //this.channel.wait();
                ChannelFuture wf = this.channel.write(message);
                //this.channel.writeAndFlush(data);
                wf.awaitUninterruptibly(CONN_TIMEOUT);

                int writeTryTimes = 1;
                Object lock = new Object();

                synchronized (lock) {
                    while (writeTryTimes < iCallRetryCnt && !wf.isSuccess()) {
                        lock.wait(10);
                        wf = channel.write(message);
                        wf.awaitUninterruptibly(CONN_TIMEOUT);
                        writeTryTimes++;
                    }
                }

                if (writeTryTimes >= iCallRetryCnt) {
                    log.error("[NettyClient][Sync] write to server failed afer retry " + iCallRetryCnt + "times");
                    return null;
                }

                long readWaited = 0L;
                ChannelHandlerContext ctx = channel.pipeline().lastContext();

                while (response == null && readWaited < CONN_TIMEOUT) {
                    Thread.sleep(1L);
                    response = getAttachment(ATTACHED_DATA_ID);
                    readWaited++;
                }

                // Remove the current attachment
                //ctx.setAttachment(null); //TODO.
                setAttachment(ATTACHED_DATA_ID, null);

                if(readWaited >= CONN_TIMEOUT) {
                    log.error("[NettyClient][Sync] Read from server failed after " + CONN_TIMEOUT + "ms");
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("[NettyClient][Sync] got a exception : {}" + e);
            return null;
        }

        return response;
    }

    private void setAttachment(String key, Object value) {
        AttributeKey attrKey = AttributeKey.valueOf(key);
        this.channel.attr(attrKey).set(value);
    }

    private Object getAttachment(String key) {
        AttributeKey attrKey = AttributeKey.valueOf(key);
        return this.channel.attr(attrKey).get();
    }
}
