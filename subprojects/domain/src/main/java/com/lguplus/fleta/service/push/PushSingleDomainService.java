package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.push.AcceptedException;
import com.lguplus.fleta.exception.push.BadRequestException;
import com.lguplus.fleta.exception.push.ExceptionOccursException;
import com.lguplus.fleta.exception.push.ForbiddenException;
import com.lguplus.fleta.exception.push.InternalErrorException;
import com.lguplus.fleta.exception.push.MaxRequestOverException;
import com.lguplus.fleta.exception.push.NotExistRegistIdException;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.exception.push.PreConditionFailedException;
import com.lguplus.fleta.exception.push.PushEtcException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import com.lguplus.fleta.exception.push.ServiceUnavailableException;
import com.lguplus.fleta.exception.push.SocketException;
import com.lguplus.fleta.exception.push.SocketTimeException;
import com.lguplus.fleta.exception.push.UnAuthorizedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSingleDomainService {

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String PUSH_COMMAND = "PUSH_NOTI";
    private static final String LG_PUSH_OLD = "LGUPUSH_OLD";
    private static final int TRANSACTION_MAX_SEQ_NO = 10000;
    private final PushConfig pushConfig;
    private final PushSingleClient pushSingleClient;
    private final AtomicInteger tranactionMsgId1 = new AtomicInteger(0);
    private final AtomicInteger tranactionMsgId2 = new AtomicInteger(0);
    private final List<ProcessCounter> pushProgressCnt = Collections.synchronizedList(new ArrayList<>());
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
    private Map<String, Object> requestLock;
    private Map<String, Object> progressLock;

    @PostConstruct
    public void initialize() {

        requestLock = pushConfig.getServiceMap();
        progressLock = pushConfig.getServiceMap();

        progressLock.forEach((serviceId, value) -> resetPushProgressCnt(serviceId));

    }

    /**
     * ??????????????????
     *
     * @param dto ????????????????????? ?????? DTO
     * @return ?????????????????? ??????
     */
    public PushClientResponseDto requestPushSingle(PushRequestSingleDto dto) {

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            log.error("ServiceId Not Found:" + dto.getServiceId());
            throw new ServiceIdNotFoundException();
        }

        // ???????????? ?????? ?????? ?????? ?????? ??????
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
            // 200 : ?????? ??????, ????????? ????????? ??????
            isFutureSuccess.set(recvStatusCode.get().equals("200") || isRetryExcludeCode(recvStatusCode.get()));
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
        // ???????????? ?????? ?????? ?????? ?????? ??????
        ImmutablePair<Long, Long> requstInfo = getPushCountInterval(serviceId);
        long pushCnt = requstInfo.getLeft();
        //long pushWaitTime = requstInfo.getRight() //Mili Seconds

        // ????????? ??????
        if (pushCnt > pushDelayReqCnt) {
            //log.debug("max-count-over : service:{} pushCnt:{}/{} wait:{}", dto.getServiceId(), pushCnt, lPushDelayReqCnt, 1000 - pushWaitTime)

            // ?????? Push ?????? ?????? ????????? ?????? ?????? ????????? 2????????? ????????? G/W??? ???????????? ?????? ????????? ?????? ???
            // (retry?????? ?????? ??????) ????????? ?????? ?????? ??????????????? ????????????.
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
                throw new PushEtcException();//("?????? ??????"); //9999
        }
    }

    private boolean isRetryExcludeCode(String code) {
        return retryExcludeCodeList.contains(code);
    }

    private boolean isLgPushServiceId(String serviceId) {
        return lgPushServceId.equals(serviceId);
    }

    private String getTransactionId(String serviceId) {
        if (!isLgPushServiceId(serviceId)) {
            return DateFormatUtils.format(new Date(), DATE_FORMAT) + String.format("%04d", tranactionMsgId1.updateAndGet(x -> (x + 1 < TRANSACTION_MAX_SEQ_NO) ? x + 1 : 0));
        } else {
            return DateFormatUtils.format(new Date(), DATE_FORMAT) + String.format("%04d", tranactionMsgId2.updateAndGet(x -> (x + 1 < TRANSACTION_MAX_SEQ_NO) ? x + 1 : 0));
        }
    }

    // Service ID ??? ?????? Push ??????
    // 1Service ID => 100/1sec
    private ImmutablePair<Long, Long> getPushCountInterval(String serviceId) {

        synchronized (requestLock.get(serviceId)) {
            long resultCnt = 0;
            long curTimeMillis = System.currentTimeMillis();
            long processCount = getPushProcessCount(serviceId);

            // Get ProcessCount, ?????? ?????? ??????
            PushStatDto pushStatDto = pushSingleClient.getPushStatus(serviceId, processCount, curTimeMillis);

            // ?????????????????? TimeGap??? ?????????
            int abnormalRequestTimeMills = 3000;
            if (curTimeMillis - pushStatDto.getMeasureStartMillis() > abnormalRequestTimeMills) {
                pushStatDto = pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
            }

            // Get ?????? ?????? ?????? ?????????
            long timeoutgap = pushStatDto.getIntervalTimeGap();
            if (pushStatDto.isIntervalOver()) {
                pushSingleClient.putPushStatus(serviceId, processCount, curTimeMillis);
                pushStatDto.setMeasurePushCount(processCount);
                pushStatDto.setMeasureStartMillis(curTimeMillis);
            } else {
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
            if (processCounter.isPresent()) {
                long setCount = processCounter.get().getTransactionCount() + changeVal;
                if (isReset) {
                    setCount = changeVal;
                }
                processCounter.get().setTransactionCount(setCount);
            } else {
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
