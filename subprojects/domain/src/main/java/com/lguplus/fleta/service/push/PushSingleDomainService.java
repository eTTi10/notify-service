package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSingleDomainService {

    private final PushConfig pushConfig;
    private final PushSingleClient pushSingleClient;

    @Value("${push-comm.push.old.lgupush.pushAppId}")
    private String oldLgPushAppId;

    @Value("${push-comm.push.old.lgupush.notiType}")
    private String oldLgPushNotiType;

    @Value("${push-comm.lgpush.service_id}")
    private String lgPushServceId;

    private AtomicInteger _transactionIDNum1 = new AtomicInteger(0);
    private AtomicInteger _transactionIDNum2 = new AtomicInteger(0);

    private Map<String,Object> requestLock;
    private Map<String,Object> progressLock;
    private Map<String, Long> pushProgressCnt;

    @PostConstruct
    public void initialize(){

        requestLock = pushConfig.getServiceMap();
        progressLock = pushConfig.getServiceMap();

        pushProgressCnt = new Hashtable<>();

        progressLock.forEach((serviceId, value) -> pushProgressCnt.put(serviceId, 0L));

    }

    /**
     * 단건푸시등록
     *
     * @param dto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public PushClientResponseDto requestPushSingle(PushRequestSingleDto dto) {
        //log.debug("PushRequestSingleDto ::::::::::::::: {}", dto);

        //1. Make Message
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_NOTI");
        paramMap.put("push_id", getTransactionId(dto.getServiceId()));
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            throw new ServiceIdNotFoundException();
        }
        paramMap.put("service_passwd", servicePwd);

        if("LGUPUSH_OLD".equals(pushConfig.getServiceLinkType(dto.getServiceId()))) {
            paramMap.put("push_app_id", oldLgPushAppId);
            paramMap.put("noti_type", oldLgPushNotiType);
            paramMap.put("regist_id", dto.getRegId());//dto.getServiceKey());
        }
        else {
            paramMap.put("service_key", dto.getRegId());//dto.getServiceKey());
        }

        dto.getItems().forEach(e -> {
            String[] item = e.split("\\!\\^");
            if(item.length >= 2){
                paramMap.put(item[0], item[1]);
            }
        });

        pushSingleClient.requestPushSingle(paramMap);

        return PushClientResponseDto.builder().build();
    }

    private boolean isLgPushServiceId(String serviceId) {
        return lgPushServceId.equals(serviceId);
    }

    private String getTransactionId(String serviceId) {
        if(!isLgPushServiceId(serviceId)) {
            if (_transactionIDNum1.get() >= 9999) {
                _transactionIDNum1.set(0);
            }
            return DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%04d", _transactionIDNum1.incrementAndGet());
        }
        else {
            if(_transactionIDNum2.get() >= 9999) {
                _transactionIDNum2.set(0);
            }
            return DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%04d", _transactionIDNum2.incrementAndGet());
        }
    }

    private ImmutablePair<Long, Long> getPushCountInterval(String serviceId) {

        synchronized(requestLock.get(serviceId)) {
            long resultCnt = 0;
            long timeoutgap = 0;
            long curTimeMillis = System.currentTimeMillis();
            long processCount = getPushProcessCount(serviceId, 0, false);

            // Get ProcessCount, 측정 기준 시간
            PushStatDto pushStatDto = pushSingleClient.getPushStatus(serviceId, processCount, curTimeMillis);

            // Get 측정 기준 시간 지나침
            timeoutgap = pushStatDto.getIntervalTimeGap();
            if(pushStatDto.isIntervalOver()) {
                pushStatDto = pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
            }
            else {
                resultCnt = pushStatDto.getMeasurePushCount();
            }

            pushSingleClient.putPushStatus(serviceId, ++resultCnt, pushStatDto.getMeasureStartMillis());

            ImmutablePair<Long, Long> pair = new ImmutablePair<>(resultCnt, timeoutgap);
            //Long key = pair.getKey();
            //Long value = pair.getValue();
            return pair;
        }

    }

    private Long getPushProcessCount(String serviceId, int changeVal, boolean reset) {

        if ( changeVal != 0 || reset) {
            synchronized (progressLock.get(serviceId)) {
                if (changeVal != 0) {
                    pushProgressCnt.put(serviceId, pushProgressCnt.get(serviceId) + changeVal);
                }
                if (reset) {
                    pushProgressCnt.put(serviceId, 0L);
                }
                return pushProgressCnt.get(serviceId);
            }
        }
        else {
            return pushProgressCnt.get(serviceId);
        }

    }

}
