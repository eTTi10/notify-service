package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;
import com.lguplus.fleta.exception.push.MaxRequestOverException;
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

    @Value("${push-comm.push.delay.reqCnt}")
    private String pushDelayReqCnt;
    private Long lPushDelayReqCnt;

    private AtomicInteger _transactionIDNum1 = new AtomicInteger(0);
    private AtomicInteger _transactionIDNum2 = new AtomicInteger(0);

    private Map<String,Object> requestLock;
    private Map<String,Object> progressLock;
    private Map<String, Long> pushProgressCnt;

    @PostConstruct
    public void initialize(){

        lPushDelayReqCnt = Long.parseLong(pushDelayReqCnt);

        requestLock = pushConfig.getServiceMap();
        progressLock = pushConfig.getServiceMap();

        pushProgressCnt = new Hashtable<>();

        progressLock.forEach((serviceId, value) -> resetPushProgressCnt(serviceId));

    }

    /**
     * 단건푸시등록
     *
     * @param dto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public PushClientResponseDto requestPushSingle(PushRequestSingleDto dto) {
        //log.debug("requestPushSingle ::::::::::::::: {}", dto);

        // 서비스별 초당 처리 건수 오류 처리
        ImmutablePair<Long, Long> requstInfo = getPushCountInterval(dto.getServiceId());
        long pushCnt = requstInfo.getLeft();
        long pushWaitTime = requstInfo.getRight();//Mili Seconds

        // 처리량 초과
        if (pushCnt > lPushDelayReqCnt) {
            log.debug("max-count-over : service:{} pushCnt:{}/{} wait:{}", dto.getServiceId(), pushCnt, lPushDelayReqCnt, 1000-pushWaitTime);

            // 현재 Push 진행 중인 갯수가 최대 허용 횟수의 2배이상 된다면 G/W가 죽었거나 뭔가 문제가 있는 것
            // (retry설정 등에 의해) 이럴땐 일단 다시 받아들이기 시작하자.
            if(lPushDelayReqCnt * 2 < pushCnt) {
                resetPushProgressCnt(dto.getServiceId());
            }
            throw new MaxRequestOverException();
            /*return PushClientResponseDto.builder()
                    .code("max-count-over")
                    .message("max-count-over")
                    .build();
             */
        }

        //1. Make Message
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_NOTI");
        paramMap.put("push_id", getTransactionId(dto.getServiceId()));
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            log.error("ServiceId Not Found:" + dto.getServiceId());
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

        setPushProgressCnt(dto.getServiceId(), +1);

        PushSingleResponseDto pushSingleResponseDto = pushSingleClient.requestPushSingle(paramMap);

        setPushProgressCnt(dto.getServiceId(), -1);

        return PushClientResponseDto.builder()
                .code(pushSingleResponseDto.getResponseData().getStatusCode())
                .message(pushSingleResponseDto.getResponseData().getStatusMsg())
                .build();
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

    // Service ID 별 초당 Push 건수
    // 1Service ID => 100/1sec
    private ImmutablePair<Long, Long> getPushCountInterval(String serviceId) {

        synchronized(requestLock.get(serviceId)) {
            long resultCnt = 0;
            long curTimeMillis = System.currentTimeMillis();
            long processCount = getPushProcessCount(serviceId);

            // Get ProcessCount, 측정 기준 시간
            PushStatDto pushStatDto = pushSingleClient.getPushStatus(serviceId, processCount, curTimeMillis);

            // 비정상적으로 TimeGap이 크다면
            if(curTimeMillis - pushStatDto.getMeasureStartMillis() > 5000) {
                pushStatDto = pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
            }

            // Get 측정 기준 시간 지나침
            long timeoutgap = pushStatDto.getIntervalTimeGap();
            if(pushStatDto.isIntervalOver()) {
                //log.debug(":: getPushCountInterval isIntervalOver timeoutgap={}", timeoutgap);
                pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
                pushStatDto.setMeasurePushCount(processCount);
                pushStatDto.setMeasureStartMillis(curTimeMillis);
            }
            else {
                resultCnt = pushStatDto.getMeasurePushCount();
                //log.debug(":: getPushCountInterval getMeasurePushCount={} timeoutgap={} currentTime={}", resultCnt, timeoutgap, curTimeMillis);
            }

            pushSingleClient.putPushStatus(serviceId, ++resultCnt, pushStatDto.getMeasureStartMillis());
            //log.debug(":: getPushCountInterval putPushStatus resultCnt={} time:{}", resultCnt, pushStatDto.getMeasureStartMillis());

            ImmutablePair<Long, Long> pair = new ImmutablePair<>(resultCnt, timeoutgap);

            return pair;
        }

    }

    //Push Count
    private Long getPushProcessCount(String serviceId) {
        synchronized (progressLock.get(serviceId)) {
            return pushProgressCnt.get(serviceId);
        }
    }

    private void setPushProgressCnt(String serviceId, int changeVal) {
        synchronized (progressLock.get(serviceId)) {
            if (changeVal != 0) {
                pushProgressCnt.put(serviceId, pushProgressCnt.get(serviceId) + changeVal);
            }
        }
    }

    private void resetPushProgressCnt(String serviceId) {
        synchronized (progressLock.get(serviceId)) {
            pushProgressCnt.put(serviceId, 0L);
        }
    }

}
