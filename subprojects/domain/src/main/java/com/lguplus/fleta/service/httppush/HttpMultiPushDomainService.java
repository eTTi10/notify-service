package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.util.HttpPushSupport;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Http Push Component
 *
 * 멀티 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpMultiPushDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpPushSupport httpPushSupport;

    @Value("${multi.push.max.tps}")
    private String maxMultiCount;

    @Value("${multi.push.reject.regList}")
    private String rejectReg;


    /**
     * 멀티푸시등록(단건푸시 사용)
     *
     * @param httpPushMultiRequestDto 멀티푸시등록을 위한 DTO
     * @return 멀티푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushMulti(HttpPushMultiRequestDto httpPushMultiRequestDto) {
        log.debug("httpPushMultiRequestDto ::::::::::::::: {}", httpPushMultiRequestDto);

        String[] results;
        String regId = "";
        String code = "";

        // Open API 에 멀티푸시를 요청한다.
        List<Future<String>> futures = requestOpenApi(httpPushMultiRequestDto);

        List<String> failUsers = new ArrayList<>();

        for (Future<String> future : futures) {
            try {
                results = future.get().split("\\|");
                regId = results[0];
                code = results[1];

            } catch (Exception ex) {
                Thread.currentThread().interrupt();

                throw new HttpPushEtcException("기타 오류");
            }

            log.debug("{} ({}) ::::::::::::::::::: {}", LocalDateTime.now(), Thread.currentThread().getName(), code);

            // Push 메시지 전송 실패
            if (!code.equals("200")) {
                log.debug("Push 메시지 전송 실패 code ::::::::::::::::::: {}", code);

                switch (code) {
                    case "202":
                        // code "1112", message "The request Accepted"
                        throw new AcceptedException();

                    case "400":
                        // code "1104", message "Push GW BadRequest"
                        throw new BadRequestException();

                    case "401":
                        // code "1105", message "Push GW UnAuthorized"
                        throw new UnAuthorizedException();

                    case "403":
                        // code "1106", message "Push GW Forbidden"
                        throw new ForbiddenException();

                    case "404":
                        // code "1107", message "Push GW Not Found"
                        throw new NotFoundException();

                        // 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip 함
                    case "410":
                    case "412":
                        log.debug("유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip 함");
                        break;

                    // 메시지 전송 실패 - Retry 대상
                    default:
                        log.debug("메시지 전송 실패 - Retry 대상");
                        failUsers.add(regId);
                        break;
                }
            }
        }

        // 성공
        if (failUsers.isEmpty()) {
            return HttpPushResponseDto.builder().build();
        }

        // 메시지 전송이 한건이라도 실패한 경우 ["1130" "메시지 전송 실패"]
        Pair<String, String> cdMsgMap = httpPushSupport.getHttpServiceProps().getExceptionCodeMessage("SendingFailedException");

        return HttpPushResponseDto.builder()
                .code(cdMsgMap.getLeft())
                .message(cdMsgMap.getRight())
                .failUsers(failUsers)
                .build();
    }

    /**
     * 초당 최대 Push 전송 허용 갯수를 가져온다.
     *
     * @param multiCount client 에서 넘어온 초당 최대 Push 전송 허용 갯수
     * @return 구해진 초당 최대 Push 전송 허용 갯수
     */
    private int setMaxLimitPush(Integer multiCount) {
        int maxLimitPush = Integer.parseInt(maxMultiCount);

        log.debug("before maxMultiCount :::::::::::: {}", maxLimitPush);

        if (multiCount != null && multiCount < maxLimitPush) {
            maxLimitPush = multiCount;
        }

        log.debug("after maxMultiCount :::::::::::: {}", maxLimitPush);

        return maxLimitPush;
    }

    /**
     * Open API 에 멀티푸시를 요청한다.(Push GW 제한 성능 문제로 동시 발송을 허용하지 않음.)
     *
     * @param httpPushMultiRequestDto 멀티푸시등록 요청 DTO
     * @return Open API 에 요청한 멀티푸시 리스트
     */
    @Synchronized
    private List<Future<String>> requestOpenApi(HttpPushMultiRequestDto httpPushMultiRequestDto) {
        // 초당 최대 Push 전송 허용 갯수
        int maxLimitPush = setMaxLimitPush(httpPushMultiRequestDto.getMultiCount());

        List<Future<String>> futures = new ArrayList<>();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        int count = 1;
        long timestamp = System.currentTimeMillis();
        String[] rejectRegIds = rejectReg.split("\\|");

        // Push 메시지 전송
        String applicationId = httpPushMultiRequestDto.getApplicationId();
        String serviceId = httpPushMultiRequestDto.getServiceId();
        String pushType = httpPushMultiRequestDto.getPushType();
        String message = httpPushMultiRequestDto.getMessage();
        List<String> items = httpPushMultiRequestDto.getItems();

        List<String> users = httpPushMultiRequestDto.getUsers();

        for (String regId : users) {
            // 사용자별 필수 값 체크 & 발송 제외 가번 확인
            if (Arrays.asList(rejectRegIds).contains(regId.strip())) {
                continue;
            }

            futures.add(executor.submit(() -> {
                        try {
                            Map<String, Object> paramMap = httpPushSupport.makePushParameters(applicationId, serviceId, pushType, message, regId, items);

                            OpenApiPushResponseDto openApiPushResponseDto = httpPushDomainClient.requestHttpPushSingle(paramMap);

                            return regId + "|" + openApiPushResponseDto.getError().get("CODE");

                        } catch (HttpPushCustomException ex) {
                            if (ex.getStatusCode() >= 500) {
                                return regId + "|" + "900";
                            }

                            return regId + "|" + ex.getStatusCode();

                        } catch (Exception ex) {
                            return regId + "|" + "900";
                        }
                    })
            );

            // TPS 설정에 따른 Time delay
            if (count % maxLimitPush == 0) {
                timestamp = delayTime(timestamp);
            }
            count++;
        }

        executor.shutdown();

        return futures;
    }

    /**
     * TPS 설정에 따라 Time 을 delay 시킨다.
     *
     * @param timestamp 요청 time
     * @return delay 후의 현재 time
     */
    private long delayTime(long timestamp) {
        long timeMillis = System.currentTimeMillis() - timestamp;

        if (timeMillis < HttpServiceProps.SECOND) {
            try {
                Thread.sleep(HttpServiceProps.SECOND - timeMillis);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();

                throw new HttpPushEtcException("기타 오류");
            }
        }

        return System.currentTimeMillis();
    }

}
