package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import com.lguplus.fleta.provider.socket.pool.PushSocketConnFactory;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Push 단건 Socket Client
 * <p>
 * 단건 푸시등록
 */
@Slf4j
@ToString
@Component
@EnableScheduling
public class PushSingleSocketClient implements PushSingleClient {

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
    private String channelPort;
    @Value("${push-comm.push.socket.channelID}")
    private String defaultChannelHost;
    @Value("${push-comm.push.cp.destination_ip}")
    private String destinationIp;
    @Value("${push-comm.push.socket.close_secend}")
    private String closeSecond;

    //LG Push
    @Value("${push-comm.lgpush.server.ip}")
    private String lg_host;
    @Value("${push-comm.lgpush.server.port}")
    private String lg_port;
    @Value("${push-comm.lgpush.socket.timeout}")
    private String lg_timeout;
    @Value("${server.port}")
    private String lg_channelPort;
    @Value("${push-comm.lgpush.socket.channelID}")
    private String lg_defaultChannelHost;
    @Value("${push-comm.lgpush.cp.destination_ip}")
    private String lg_destinationIp;
    @Value("${push-comm.lgpush.socket.close_secend}")
    private String lg_closeSecond;

    //LG Push Service ID
    @Value("${push-comm.lgpush.service_id}")
    private String lgPushServiceId;

    @Value("${push-comm.push.call.retryCnt}")
    private String pushCallRetryCnt;
    private int iPushCallRetryCnt;

    //Pool
    private List<GenericObjectPool<PushSocketInfo>> poolList;

    /**
     * Push Single 푸시
     *
     * @param paramMap Push Single 푸시 정보
     * @return Push Single 푸시 결과
     */
    @Override
    public PushSingleResponseDto requestPushSingle(Map<String, String> paramMap) {

        PushSocketInfo socketInfo = null;
        boolean bIsLgPush = lgPushServiceId.equals(paramMap.get("service_id"));

        GenericObjectPool<PushSocketInfo> pool = poolList.stream().filter(p->((PushSocketConnFactory)p.getFactory()).isLgPush() == bIsLgPush)
                .findFirst().get();

        try {
            socketInfo = pool.borrowObject();
            PushAnnounceResponseDto retDto = socketInfo.sendPushNotice(paramMap);

            return getSingleResponseDto(retDto.getResponseAnnouncement().getStatusCode(), retDto.getResponseAnnouncement().getStatusMsg());

        } catch (NoSuchElementException e) {
            //e.printStackTrace();
            log.error(e.toString());
            return getSingleResponseDto("500", "Exception Occurs");
        } catch (PushBizException e) {
            //e.printStackTrace();
            log.error(e.toString());
            return getSingleResponseDto("503", "Service Unavailable");
        } catch (Exception e) {
            log.error(e.toString());
            return getSingleResponseDto("500", "Internal Error");
        } finally {
            if (socketInfo != null)
                pool.returnObject(socketInfo);
        }

    }

    @PostConstruct
    public void initialize() {

        poolList = new ArrayList<>();
        poolList.add(new GenericObjectPool<PushSocketInfo>(
                new PushSocketConnFactory(host, port, timeout, channelPort, defaultChannelHost, destinationIp, closeSecond, false)
                , getPoolConfig(Integer.valueOf(socketMax), Integer.valueOf(socketMin))));
/*
        poolList.add(new GenericObjectPool<PushSocketInfo>(
                new PushSocketConnFactory(lg_host, lg_port, lg_timeout, lg_channelPort, lg_defaultChannelHost, lg_destinationIp, lg_closeSecond, true)
                , getPoolConfig(Integer.valueOf(lgSocketMax), Integer.valueOf(lgSocketMin))));
*/
        iPushCallRetryCnt = Integer.valueOf(pushCallRetryCnt);

    }

    @Scheduled(fixedDelay = 1000 * 20)
    public void socketClientSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);

        poolList.forEach(p -> {
            //p.evict();
            log.info("socketClientSch: Hdtv Time:{} Active{}/Idle{}/Wait{}", strDate,
                    p.getNumActive(), p.getNumIdle(), p.getNumWaiters());
        });
    }

    @PreDestroy
    public void destroy() {
        log.debug(":::::::::::::: PushSingleDomainSocketClient Clear/Close");
        poolList.forEach(p -> p.close());

        log.debug(":::::::::::::: PushSingleDomainSocketClient Clear/Close ...");
    }

    private PushSingleResponseDto getSingleResponseDto(String code, String msg) {
        return PushSingleResponseDto.builder().responseData(PushSingleResponseDto.ResponseSingle.builder().statusCode(code).statusMsg(msg).build()).build();
    }

    private GenericObjectPoolConfig<PushSocketInfo> getPoolConfig(int maxTotal, int minIdle) {
        GenericObjectPoolConfig<PushSocketInfo> poolConfig = new GenericObjectPoolConfig<PushSocketInfo>();
        poolConfig.setJmxEnabled(false);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setBlockWhenExhausted(false);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(30 * 1000);

        return poolConfig;
    }


}
