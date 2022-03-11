package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
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
    @Value("${push-comm.push.socket.max}")
    private String socketMax;
    @Value("${push-comm.push.socket.min}")
    private String socketMin;
    @Value("${push-comm.lgpush.socket.max}")
    private String lgSocketMax;
    @Value("${push-comm.lgpush.socket.min}")
    private String lgSocketMin;

    //HDTV
    @Value("${push-comm.push.server.ip}")
    private String host;
    @Value("${push-comm.push.server.port}")
    private String port;
    @Value("${push-comm.push.socket.timeout}")
    private String timeout;
    @Value("${server.port}")
    private String wasPort;
    @Value("${push-comm.push.socket.channelID}")
    private String defaultChannelHost;
    @Value("${push-comm.push.cp.destination_ip}")
    private String destinationIp;
    @Value("${push-comm.push.socket.close_secend}")
    private String closeSecond;
    @Value("${push-comm.push.socket.initCnt}")
    private String pushSocketInitCnt;

    //LG Push
    @Value("${push-comm.lgpush.server.ip}")
    private String lgHost;
    @Value("${push-comm.lgpush.server.port}")
    private String lgPort;
    @Value("${push-comm.lgpush.socket.timeout}")
    private String lgTimeout;
    @Value("${push-comm.lgpush.socket.channelID}")
    private String lgDefaultChannelHost;
    @Value("${push-comm.lgpush.cp.destination_ip}")
    private String lgDestinationIp;
    @Value("${push-comm.lgpush.socket.close_secend}")
    private String lgCloseSecond;
    @Value("${push-comm.lgpush.socket.initCnt}")
    private String lgPushSocketInitCnt;

    //LG Push Service ID
    @Value("${push-comm.lgpush.service_id}")
    private String lgPushServiceId;

    @Value("${push-comm.push.delay.time}")
    private String pushIntervalTime;
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
                .host(host).port(Integer.parseInt(port)).timeout(Integer.parseInt(timeout)).channelPort(Integer.parseInt(wasPort))
                .defaultChannelHost(defaultChannelHost).closeSecond(Integer.parseInt(closeSecond)).destinationIp(destinationIp)
                .isLgPush(false).build();

        PushSocketConnFactory.PushServerInfoVo pushServerInfoVoLg = PushSocketConnFactory.PushServerInfoVo.builder()
                .host(lgHost).port(Integer.parseInt(lgPort)).timeout(Integer.parseInt(lgTimeout)).channelPort(Integer.parseInt(wasPort))
                .defaultChannelHost(lgDefaultChannelHost).closeSecond(Integer.parseInt(lgCloseSecond)).destinationIp(lgDestinationIp)
                .isLgPush(true).build();

        socketPools = new ArrayList<>();

        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedTimeout(250);

        socketPools.add(new GenericObjectPool<>(
                new PushSocketConnFactory(pushServerInfoVo)
                , getPoolConfig(Integer.parseInt(socketMax), Integer.parseInt(socketMin) ), abandonedConfig));

        socketPools.add(new GenericObjectPool<>(
                new PushSocketConnFactory(pushServerInfoVoLg)
                , getPoolConfig(Integer.parseInt(lgSocketMax), Integer.parseInt(lgSocketMin)), abandonedConfig));

        measureIntervalMillis = Integer.parseInt(pushIntervalTime) * 1000L;

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
    @Cacheable(value="PUSH_CACHE", key="'statistics1.'+#serviceId")
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
    @CachePut(value="PUSH_CACHE", key="'statistics1.'+#serviceId")
    public PushStatDto putPushStatus(String serviceId, long measurePushCount, long measureStartMillis) {

        return PushStatDto.builder()
                .serviceId(serviceId)
                .measureIntervalMillis(measureIntervalMillis)
                .measurePushCount(measurePushCount)
                .measureStartMillis(measureStartMillis)
                .build();
    }

}