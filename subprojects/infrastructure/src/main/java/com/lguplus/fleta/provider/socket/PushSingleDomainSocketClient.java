package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushSingleDomainClient;
import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import com.lguplus.fleta.provider.socket.pool.PushSocketConnFactory;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.RequiredArgsConstructor;
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
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class PushSingleDomainSocketClient implements PushSingleDomainClient {

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

    //Pool
    private GenericObjectPool<PushSocketInfo> hdtvPool;
    private GenericObjectPool<PushSocketInfo> lgPushPool;

    /**
     * Push Announcement 푸시
     *
     * @param paramMap Push Announcement 푸시 정보
     * @return Push Announcement 푸시 결과
     */
    @Override
    public PushSingleResponseDto requestPushSingle(Map<String, String> paramMap) {

        PushSocketInfo socketInfo = null;
        GenericObjectPool<PushSocketInfo> pool = (lgPushServiceId.equals(paramMap.get("service_id"))) ? lgPushPool : hdtvPool;

        try
        {
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
            if(socketInfo != null)
                pool.returnObject(socketInfo);
        }
    }

    @PostConstruct
    public void initialize() {

        GenericObjectPoolConfig poolConfigHdtv = new GenericObjectPoolConfig();
        poolConfigHdtv.setJmxEnabled(false);
        poolConfigHdtv.setMaxTotal(Integer.valueOf(socketMax));
        poolConfigHdtv.setMinIdle(Integer.valueOf(socketMin));
        poolConfigHdtv.setBlockWhenExhausted(false);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfigHdtv.setTestOnBorrow(true);
        poolConfigHdtv.setTestOnReturn(true);
        poolConfigHdtv.setTestWhileIdle(true);
        poolConfigHdtv.setTimeBetweenEvictionRunsMillis(30 * 1000);

        PushSocketConnFactory hdtvFactory = new PushSocketConnFactory(host, port, timeout, channelPort, defaultChannelHost, destinationIp, closeSecond);

        this.hdtvPool = new GenericObjectPool<PushSocketInfo>(hdtvFactory, poolConfigHdtv);

        GenericObjectPoolConfig poolConfigLg = new GenericObjectPoolConfig();
        poolConfigLg.setJmxEnabled(false);
        poolConfigLg.setMaxTotal(Integer.valueOf(lgSocketMax));
        poolConfigLg.setMinIdle(Integer.valueOf(lgSocketMin));
        poolConfigLg.setBlockWhenExhausted(false);//풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청 시, true 이면 대기, false 이면 NoSuchElementException 발생
        poolConfigLg.setTestOnBorrow(true);
        poolConfigLg.setTestOnReturn(true);
        poolConfigLg.setTestWhileIdle(true);
        poolConfigLg.setTimeBetweenEvictionRunsMillis(30 * 1000);

        PushSocketConnFactory lgFactory = new PushSocketConnFactory(lg_host, lg_port, lg_timeout, lg_channelPort, lg_defaultChannelHost, lg_destinationIp, lg_closeSecond);

        this.lgPushPool = new GenericObjectPool<PushSocketInfo>(lgFactory, poolConfigLg);
    }

    @Scheduled(fixedDelay = 1000 * 20)
    public void socketClientSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        //log.info("Fixed rate task :: {}" ,strDate);

        /*
        try {
            hdtvPool.evict();
        } catch (Exception e) {
            e.printStackTrace();        }
        */

        log.info("socketClientSch: Hdtv Time:{} Active{}/Idle{}/Wait{}", strDate,
                hdtvPool.getNumActive(), hdtvPool.getNumIdle(), hdtvPool.getNumWaiters());
    }

    @PreDestroy
    public void destroy() {
        log.debug(":::::::::::::: PushSingleDomainSocketClient Clear/Close");
        hdtvPool.close();
        lgPushPool.close();
        log.debug(":::::::::::::: PushSingleDomainSocketClient Clear/Close ...");
    }

    private PushSingleResponseDto getSingleResponseDto(String code, String msg) {
        return PushSingleResponseDto.builder().responseData(PushSingleResponseDto.ResponseSingle.builder().statusCode(code).statusMsg(msg).build()).build();
    }

}
