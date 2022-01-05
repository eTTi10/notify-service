package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.*;
import lombok.*;
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

    @Value("${push-comm.push.call.retryCnt}")
    private String pushCallRetryCnt;
    private int iPushCallRetryCnt;

    @Value("${push-comm.retry.exclud.codeList}")
    private String retryExcludeCodeList;

    private final AtomicInteger tranactionMsgId1 = new AtomicInteger(0);
    private final AtomicInteger tranactionMsgId2 = new AtomicInteger(0);

    private Map<String,Object> requestLock;
    private Map<String,Object> progressLock;
    private final List<ProcessCounter> pushProgressCnt = new ArrayList<>();

    private static final String DATE_FOMAT = "yyyyMMdd";
    private static final String PUSH_COMMAND = "PUSH_NOTI";
    private static final String LG_PUSH_OLD = "LGUPUSH_OLD";
    private static final int TRANSACTION_MAX_SEQ_NO = 10000;

    @PostConstruct
    public void initialize(){

        lPushDelayReqCnt = Long.parseLong(pushDelayReqCnt);
        iPushCallRetryCnt = Integer.parseInt(pushCallRetryCnt);

        requestLock = pushConfig.getServiceMap();
        progressLock = pushConfig.getServiceMap();

        progressLock.forEach((serviceId, value) -> resetPushProgressCnt(serviceId));

    }

    /**
     * 단건푸시등록
     *
     * @param dto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public PushClientResponseDto requestPushSingle(PushRequestSingleDto dto) {

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            log.error("ServiceId Not Found:" + dto.getServiceId());
            throw new ServiceIdNotFoundException();
        }

        // 서비스별 초당 처리 건수 오류 처리
        checkThroughput(dto.getServiceId());

        //1. Make Message
        Map<String, String> paramMap = getMessage(dto, servicePwd);

        String statusCode = "";
        String statusMsg = "";

        //2. Send Push
        int reCnt = 0;
        while (reCnt < iPushCallRetryCnt) {
            try {
                PushResponseDto pushResponseDto;
                setPushProgressCnt(dto.getServiceId(), +1);
                pushResponseDto = pushSingleClient.requestPushSingle(paramMap);

                statusCode = pushResponseDto.getStatusCode();
                statusMsg = pushResponseDto.getStatusMsg();
            } finally {
                setPushProgressCnt(dto.getServiceId(), -1);
            }

            //3. Send Result
            if (statusCode.equals("200")) {
                log.trace("[requestPushSingle][" + statusCode + "] [SUCCESS]");
                break;
            }

            log.debug("[requestPushSingle][" + statusCode + "] [FAIL] retry:{}/{} ", reCnt+1, iPushCallRetryCnt);

            // 재시도 예외 리턴 코드인 경우 재시도 안함.
            // 재시도 횟수에 도달한 경우 바로 반환.
            if (++reCnt == iPushCallRetryCnt || isRetryExcludeCode(statusCode)) {
                throw exceptionHandler(statusCode);
                //테스트 코드
                // return PushClientResponseDto.builder().code("503").message("Failure").build()
            }
        }

        return PushClientResponseDto.builder().code(statusCode).message(statusMsg)
                .build();
    }

    private Map<String, String> getMessage(PushRequestSingleDto dto, String servicePwd) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PUSH_COMMAND);
        paramMap.put("push_id", getTransactionId(dto.getServiceId()));
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());
        paramMap.put("service_passwd", servicePwd);

        if (LG_PUSH_OLD.equals(pushConfig.getServiceLinkType(dto.getServiceId()))) {
            paramMap.put("push_app_id", oldLgPushAppId);
            paramMap.put("noti_type", oldLgPushNotiType);
            paramMap.put("regist_id", dto.getRegId());
        } else {
            paramMap.put("service_key", dto.getRegId());
        }

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        return paramMap;

    }

    private void checkThroughput(String serviceId) {
        // 서비스별 초당 처리 건수 오류 처리
        ImmutablePair<Long, Long> requstInfo = getPushCountInterval(serviceId);
        long pushCnt = requstInfo.getLeft();
        //long pushWaitTime = requstInfo.getRight() //Mili Seconds

        // 처리량 초과
        if (pushCnt > lPushDelayReqCnt) {
            //log.debug("max-count-over : service:{} pushCnt:{}/{} wait:{}", dto.getServiceId(), pushCnt, lPushDelayReqCnt, 1000 - pushWaitTime)

            // 현재 Push 진행 중인 갯수가 최대 허용 횟수의 2배이상 된다면 G/W가 죽었거나 뭔가 문제가 있는 것
            // (retry설정 등에 의해) 이럴땐 일단 다시 받아들이기 시작하자.
            if (lPushDelayReqCnt * 2 < pushCnt) {
                resetPushProgressCnt(serviceId);
            }
            throw new MaxRequestOverException();
        }
    }

    private NotifyPushRuntimeException exceptionHandler(String statusCode) {
        switch (statusCode) {
            case "202":
                return new AcceptedException();
            case "400":
                return new BadRequestException();
            case "401":
                return new UnAuthorizedException();
            case "403":
                return new ForbiddenException();
            case "404":
                return new NotFoundException();
            case "410":
                return new NotExistRegistIdException();
            case "412":
                return new PreConditionFailedException();
            case "500":
                return new InternalErrorException();
            case "502":
                return new ExceptionOccursException();
            case "503":
                return new ServiceUnavailableException();
            case "5102":
                return new SocketTimeException();
            case "5103": //FeignException
                return new SocketException();
            default:
                return new PushEtcException();//("기타 오류"); //9999
        }
    }

    private boolean isRetryExcludeCode(String code) {
        return ("|"+retryExcludeCodeList+"|").contains("|" + code+"|");
    }

    private boolean isLgPushServiceId(String serviceId) {
        return lgPushServceId.equals(serviceId);
    }

    private String getTransactionId(String serviceId) {
        if(!isLgPushServiceId(serviceId)) {
            return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId1.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0));
        }
        else {
            return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId2.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0));
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
            int abnormalRequestTimeMills = 3000;
            if(curTimeMillis - pushStatDto.getMeasureStartMillis() > abnormalRequestTimeMills) {
                pushStatDto = pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
            }

            // Get 측정 기준 시간 지나침
            long timeoutgap = pushStatDto.getIntervalTimeGap();
            if(pushStatDto.isIntervalOver()) {
                pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
                pushStatDto.setMeasurePushCount(processCount);
                pushStatDto.setMeasureStartMillis(curTimeMillis);
            }
            else {
                resultCnt = pushStatDto.getMeasurePushCount();
                //log.debug(":: getPushCountInterval getMeasurePushCount={} timeoutgap={} currentTime={}", resultCnt, timeoutgap, curTimeMillis)
            }

            pushSingleClient.putPushStatus(serviceId, ++resultCnt, pushStatDto.getMeasureStartMillis());
            //log.debug(":: getPushCountInterval putPushStatus resultCnt={} time:{}", resultCnt, pushStatDto.getMeasureStartMillis())

            return new ImmutablePair<>(resultCnt, timeoutgap);
        }

    }

    //Push Count
    private Long getPushProcessCount(String serviceId) {
        synchronized (progressLock.get(serviceId)) {
            Optional<ProcessCounter> processCounter = pushProgressCnt.stream().filter(t -> t.getServiceId().equals(serviceId)).findAny();
            return processCounter.isPresent() ? processCounter.get().getTransactionCount() : 0;
        }
    }

    private void setPushProgressCnt(String serviceId, int changeVal) {
        synchronized (progressLock.get(serviceId)) {
            Optional<ProcessCounter> processCounter = pushProgressCnt.stream().filter(t -> t.getServiceId().equals(serviceId)).findAny();
            if(processCounter.isPresent()) {
                processCounter.get().setTransactionCount(processCounter.get().getTransactionCount() + changeVal);
            }
            else {
                pushProgressCnt.add(ProcessCounter.builder().serviceId(serviceId).transactionCount((long) changeVal).build());
            }
        }
    }

    private void resetPushProgressCnt(String serviceId) {
        synchronized (progressLock.get(serviceId)) {
           Optional<ProcessCounter> processCounter = pushProgressCnt.stream().filter(t -> t.getServiceId().equals(serviceId)).findAny();
           if(processCounter.isPresent()) {
               processCounter.get().setTransactionCount(0L);
           }
           else {
               pushProgressCnt.add(ProcessCounter.builder().serviceId(serviceId).transactionCount(0L).build());
           }
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    static class ProcessCounter {
        private String serviceId;
        private Long transactionCount;

        public void setTransactionCount(Long transactionCount) {
            this.transactionCount = transactionCount;
        }
    }
}
