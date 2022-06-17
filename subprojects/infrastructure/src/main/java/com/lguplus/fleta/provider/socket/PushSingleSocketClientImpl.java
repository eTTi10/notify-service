package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.type.CacheName;
import com.lguplus.fleta.provider.socket.pool.PushSocketConnFactory;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Push 단건 Socket Client
 * <p>
 * 단건 푸시등록
 */
@Slf4j
@ToString
@Component
@EnableScheduling
public class PushSingleSocketClientImpl implements PushSingleClient {

    //Pool Config
    @Value("${push.gateway.default.socket.max}")
    private int socketMax;
    @Value("${push.gateway.default.socket.min}")
    private int socketMin;
    @Value("${push.gateway.lgupush.socket.max}")
    private int lgSocketMax;
    @Value("${push.gateway.lgupush.socket.min}")
    private int lgSocketMin;

    //HDTV
    @Value("${push.gateway.default.ip}")
    private String host;
    @Value("${push.gateway.default.port}")
    private int port;
    @Value("${push.gateway.default.socket.timeout}")
    private int timeout;
    @Value("${server.port}")
    private int wasPort;
    @Value("${push.gateway.default.socket.channelId}")
    private String defaultChannelHost;
    @Value("${push.gateway.default.destination}")
    private String destinationIp;
    @Value("${push.gateway.default.socket.closeSecond}")
    private int closeSecond;
    @Value("${push.gateway.default.socket.init}")
    private int pushSocketInitCnt;

    //LG Push
    @Value("${push.gateway.lgupush.ip}")
    private String lgHost;
    @Value("${push.gateway.lgupush.port}")
    private int lgPort;
    @Value("${push.gateway.lgupush.socket.timeout}")
    private int lgTimeout;
    @Value("${push.gateway.lgupush.socket.channelId}")
    private String lgDefaultChannelHost;
    @Value("${push.gateway.lgupush.destination}")
    private String lgDestinationIp;
    @Value("${push.gateway.lgupush.socket.closeSecond}")
    private int lgCloseSecond;
    @Value("${push.gateway.lgupush.socket.init}")
    private int lgPushSocketInitCnt;

    //LG Push Service ID
    @Value("${push.gateway.serviceId}")
    private String lgPushServiceId;

    @Value("${push.gateway.delay.time}")
    private int pushIntervalTime;
    private long measureIntervalMillis;

    //Pool
    private List<GenericObjectPool<PushSocketInfo>> socketPools;

    private static final int HDTV_PUSH_IDX = 0;
    private static final int LG_PUSH_IDX = 1;
    private static final int EXTRA_CONN_COUNT = 50;

    /**
     * Push Single 푸시
     *
     * @param paramMap Push Single 푸시 정보
     * @return Push Single 푸시 결과
     */
    @Override
    public PushResponseDto requestPushSingle(Map<String, String> paramMap) {

        PushSocketInfo socketInfo = null;
        boolean bIsLgPush = lgPushServiceId.equals(paramMap.get("service_id"));

        GenericObjectPool<PushSocketInfo> socketPool = socketPools.get(bIsLgPush ? LG_PUSH_IDX : HDTV_PUSH_IDX);

        try
        {
            socketInfo = socketPool.borrowObject();

            PushResponseDto retDto = socketInfo.sendPushNotice(paramMap);

            return PushResponseDto.builder().statusCode(retDto.getStatusCode()).statusMsg(retDto.getStatusMsg()).build();

        } catch (Exception e) {
            log.error(e.toString());
            return PushResponseDto.builder().statusCode("500").statusMsg("Internal Error").build();
        } finally {
            if (socketInfo != null)
                socketPool.returnObject(socketInfo);
        }

    }

    @PostConstruct
    private void initialize() {

        PushSocketConnFactory.PushServerInfoVo pushServerInfoVo = PushSocketConnFactory.PushServerInfoVo.builder()
                .host(host).port(port).timeout(timeout).channelPort(wasPort)
                .defaultChannelHost(defaultChannelHost).closeSecond(closeSecond).destinationIp(destinationIp)
                .isLgPush(false).build();

        PushSocketConnFactory.PushServerInfoVo pushServerInfoVoLg = PushSocketConnFactory.PushServerInfoVo.builder()
                .host(lgHost).port(lgPort).timeout(lgTimeout).channelPort(wasPort)
                .defaultChannelHost(lgDefaultChannelHost).closeSecond(lgCloseSecond).destinationIp(lgDestinationIp)
                .isLgPush(true).build();

        socketPools = new ArrayList<>();

        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedTimeout(250);

        socketPools.add(new GenericObjectPool<>(
                new PushSocketConnFactory(pushServerInfoVo)
                , getPoolConfig(socketMax, socketMin), abandonedConfig));

        socketPools.add(new GenericObjectPool<>(
                new PushSocketConnFactory(pushServerInfoVoLg)
                , getPoolConfig(lgSocketMax, lgSocketMin), abandonedConfig));

        measureIntervalMillis = pushIntervalTime * 1000L;

    }

    @Scheduled(fixedDelay = 1000 * 10)
    public void socketClientSch() {
        socketPools.forEach(p -> log.trace("socketClientSch: Hdtv Time:{} Active{}/Idle{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), p.getNumActive(), p.getNumIdle()));
    }

    @PreDestroy
    private void destroy() {
        socketPools.forEach(GenericObjectPool::close);
        log.debug(":::::::::::::: PushSingleDomainSocketClient Clear/Close ...");
    }

    private GenericObjectPoolConfig<PushSocketInfo> getPoolConfig(int maxTotal, int minIdle) {
        GenericObjectPoolConfig<PushSocketInfo> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(maxTotal+EXTRA_CONN_COUNT);
        poolConfig.setMaxIdle(maxTotal);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setBlockWhenExhausted(true);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfig.setMaxWaitMillis(2000);// 최대 대기 시간
        poolConfig.setTestOnCreate(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setLifo(false); //false : FIFO, default: LIFO :: FIFO(소켓이 만들어지거나 사용되어진 시간 기준으로 오래된 자원부터 사용)
        poolConfig.setTimeBetweenEvictionRunsMillis(10 * 1000L);

        return poolConfig;
    }

    @Override
    @Cacheable(value=CacheName.PUSH_STATISTICS, key="#serviceId")
    public PushStatDto getPushStatus(String serviceId, long measurePushCount, long measureStartMillis) {
        log.debug("getPushStatus: init : " + System.currentTimeMillis());

        return PushStatDto.builder()
                .serviceId(serviceId)
                .measureIntervalMillis(measureIntervalMillis)
                .measurePushCount(measurePushCount)
                .measureStartMillis(measureStartMillis)
                .build();
    }

    @Override
    @CachePut(value=CacheName.PUSH_STATISTICS, key="#serviceId")
    public PushStatDto putPushStatus(String serviceId, long measurePushCount, long measureStartMillis) {

        return PushStatDto.builder()
                .serviceId(serviceId)
                .measureIntervalMillis(measureIntervalMillis)
                .measurePushCount(measurePushCount)
                .measureStartMillis(measureStartMillis)
                .build();
    }

}
