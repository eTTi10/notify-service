package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.push.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSingleDomainService {

    private final PushConfig pushConfig;
    private final PushSingleClient pushSingleClient;

    @Value("${push.gateway.appId}")
    private String oldLgPushAppId;

    @Value("${push.gateway.notiType}")
    private String oldLgPushNotiType;

    @Value("${push.gateway.serviceId}")
    private String lgPushServceId;

    @Value("${push.gateway.delay.request}")
    private long pushDelayReqCnt;

    @Value("${push.gateway.retry.count}")
    private int pushCallRetryCnt;

    @Value("${push.gateway.retry.exclude}")
    private Set<String> retryExcludeCodeList;

    private final AtomicInteger tranactionMsgId1 = new AtomicInteger(0);
    private final AtomicInteger tranactionMsgId2 = new AtomicInteger(0);

    private Map<String,Object> requestLock;
    private Map<String,Object> progressLock;
    private final List<ProcessCounter> pushProgressCnt = Collections.synchronizedList(new ArrayList<>());

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String PUSH_COMMAND = "PUSH_NOTI";
    private static final String LG_PUSH_OLD = "LGUPUSH_OLD";
    private static final int TRANSACTION_MAX_SEQ_NO = 10000;

    @PostConstruct
    public void initialize(){

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

        int retryCount = Optional.ofNullable(dto.getRetryCount()).orElse(0) > 1 ? dto.getRetryCount() : pushCallRetryCnt;

        //2. Send Push
        AtomicBoolean isFutureSuccess = new AtomicBoolean(false);
        AtomicReference<String> recvStatusCode = new AtomicReference<>("");
        AtomicReference<String> recvStatusMsg = new AtomicReference<>("");

        IntStream.range(0, retryCount).takeWhile(value -> !isFutureSuccess.get()).forEach(reCnt -> {
            try {
                setPushProgressCnt(dto.getServiceId(), +1);
                PushResponseDto pushResponseDto = pushSingleClient.requestPushSingle(paramMap);

                recvStatusCode.set(pushResponseDto.getStatusCode());
                recvStatusMsg.set(pushResponseDto.getStatusMsg());
            } finally {
                setPushProgressCnt(dto.getServiceId(), -1);
            }

            // 200 : 정상 처리, 재시도 예외인 경우
            isFutureSuccess.set( recvStatusCode.get().equals("200") || isRetryExcludeCode(recvStatusCode.get()) );
        });

        //test
        //statusCode = "200"
        exceptionHandler(recvStatusCode.get());

        return PushClientResponseDto.builder().code(recvStatusCode.get()).message(recvStatusMsg.get()).build();
    }

    private Map<String, String> getMessage(PushRequestSingleDto dto, String servicePwd) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PUSH_COMMAND);
        paramMap.put("push_id", getTransactionId(dto.getServiceId()));
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
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
        if (pushCnt > pushDelayReqCnt) {
            //log.debug("max-count-over : service:{} pushCnt:{}/{} wait:{}", dto.getServiceId(), pushCnt, lPushDelayReqCnt, 1000 - pushWaitTime)

            // 현재 Push 진행 중인 갯수가 최대 허용 횟수의 2배이상 된다면 G/W가 죽었거나 뭔가 문제가 있는 것
            // (retry설정 등에 의해) 이럴땐 일단 다시 받아들이기 시작하자.
            if (pushDelayReqCnt * 2 < pushCnt) {
                resetPushProgressCnt(serviceId);
            }
            throw new MaxRequestOverException();
        }
    }

    private void exceptionHandler(String statusCode) throws NotifyRuntimeException {
        switch (statusCode) {
            case "200":
                break;
            case "202":
                throw new AcceptedException();
            case "400":
                throw new BadRequestException();
            case "401":
                throw new UnAuthorizedException();
            case "403":
                throw new ForbiddenException();
            case "404":
                throw new NotFoundException();
            case "410":
                throw new NotExistRegistIdException();
            case "412":
                throw new PreConditionFailedException();
            case "500":
                throw new InternalErrorException();
            case "502":
                throw new ExceptionOccursException();
            case "503":
                throw new ServiceUnavailableException();
            case "5102":
                throw new SocketTimeException();
            case "5103": //FeignException
                throw new SocketException();
            default:
                throw new PushEtcException();//("기타 오류"); //9999
        }
    }

    private boolean isRetryExcludeCode(String code) {
        return retryExcludeCodeList.contains(code);
    }

    private boolean isLgPushServiceId(String serviceId) {
        return lgPushServceId.equals(serviceId);
    }

    private String getTransactionId(String serviceId) {
        if(!isLgPushServiceId(serviceId)) {
            return DateFormatUtils.format(new Date(), DATE_FORMAT) + String.format("%04d", tranactionMsgId1.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0));
        }
        else {
            return DateFormatUtils.format(new Date(), DATE_FORMAT) + String.format("%04d", tranactionMsgId2.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0));
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
            return processCounter.orElse(ProcessCounter.builder().transactionCount(0L).build()).getTransactionCount();
        }
    }

    private void setPushProgressCnt(String serviceId, int changeVal) {
        setPushProgressCnt(serviceId, changeVal, false);
    }

    private void setPushProgressCnt(String serviceId, int changeVal, boolean isReset) {
        synchronized (progressLock.get(serviceId)) {
            Optional<ProcessCounter> processCounter = pushProgressCnt.stream().filter(t -> t.getServiceId().equals(serviceId)).findAny();
            if(processCounter.isPresent()) {
                long setCount = processCounter.get().getTransactionCount() + changeVal;
                if(isReset) {
                    setCount = changeVal;
                }
                processCounter.get().setTransactionCount(setCount);
            }
            else {
                pushProgressCnt.add(ProcessCounter.builder().serviceId(serviceId).transactionCount(changeVal * 1L).build());
            }
        }
    }

    private void resetPushProgressCnt(String serviceId) {
        setPushProgressCnt(serviceId, 0, true);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @ToString
    static class ProcessCounter {
        private String serviceId;
        private Long transactionCount;

        public void setTransactionCount(Long transactionCount) {
            this.transactionCount = transactionCount;
        }
    }
}
