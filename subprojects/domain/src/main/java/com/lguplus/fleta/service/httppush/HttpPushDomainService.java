package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.util.HttpPushSupport;
import lombok.RequiredArgsConstructor;
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
 * 단건, 멀티 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpPushSupport httpPushSupport;

    private final Object lock = new Object();

    @Value("${multi.push.max.tps}")
    private String maxMultiCount;

    @Value("${multi.push.reject.regList}")
    private String rejectReg;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        log.debug("httpPushSingleRequestDto ::::::::::::::: {}", httpPushSingleRequestDto);

        // 발송 제외 가번 확인
        log.debug("rejectReg :::::::::::::::::::: {}", rejectReg);
        String[] rejectRegList = rejectReg.split("\\|");
        String regId = httpPushSingleRequestDto.getUsers().get(0);

        if (Arrays.asList(rejectRegList).contains(regId.strip())) {
            Pair<String, String> cdMsgMap = httpPushSupport.getHttpServiceProps().getExceptionCodeMessage("ExclusionNumberException");

            HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
            httpPushCustomException.setCode(cdMsgMap.getLeft());
            httpPushCustomException.setMessage(cdMsgMap.getRight());

            throw httpPushCustomException;   // 9998 발송제한번호
        }

        String appId = httpPushSingleRequestDto.getAppId();
        String serviceId = httpPushSingleRequestDto.getServiceId();
        String pushType = httpPushSingleRequestDto.getPushType();
        String msg = httpPushSingleRequestDto.getMsg();
        List<String> items = httpPushSingleRequestDto.getItems();

        Map<String, Object> paramMap = httpPushSupport.makePushParameters(appId, serviceId, pushType, msg, regId, items);

        httpPushDomainClient.requestHttpPushSingle(paramMap);

        // 성공
        return HttpPushResponseDto.builder().build();
    }

    /**
     * 멀티푸시등록(단건푸시 사용)
     *
     * @param httpPushMultiRequestDto 멀티푸시등록을 위한 DTO
     * @return 멀티푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushMulti(HttpPushMultiRequestDto httpPushMultiRequestDto) {
        log.debug("httpPushMultiRequestDto ::::::::::::::: {}", httpPushMultiRequestDto);

        // 초당 최대 Push 전송 허용 갯수
        Integer maxLimitPush = Integer.parseInt(maxMultiCount);

        log.debug("before maxMultiCount :::::::::::: {}", maxLimitPush);

        if (httpPushMultiRequestDto.getMultiCount() != null && httpPushMultiRequestDto.getMultiCount() < maxLimitPush) {
            maxLimitPush = httpPushMultiRequestDto.getMultiCount();
        }

        log.debug("after maxMultiCount :::::::::::: {}", maxLimitPush);

        List<String> failUsers = new ArrayList<>();
        List<Future<String>> resultList = new ArrayList<>();

        // Push GW 제한 성능 문제로 동시 발송을 허용하지 않음.
        synchronized (lock) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            int count = 1;
            long timestamp = System.currentTimeMillis();
            String[] rejectRegList = rejectReg.split("\\|");

            // Push 메시지 전송
            String appId = httpPushMultiRequestDto.getAppId();
            String serviceId = httpPushMultiRequestDto.getServiceId();
            String pushType = httpPushMultiRequestDto.getPushType();
            String msg = httpPushMultiRequestDto.getMsg();
            List<String> items = httpPushMultiRequestDto.getItems();

            List<String> users = httpPushMultiRequestDto.getUsers();

            for (String regId : users) {
                // 사용자별 필수 값 체크 & 발송 제외 가번 확인
                if (Arrays.asList(rejectRegList).contains(regId.strip())) {
                    continue;
                }

                resultList.add(executor.submit(() -> {
                            try {
                                Map<String, Object> paramMap = httpPushSupport.makePushParameters(appId, serviceId, pushType, msg, regId, items);

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
                    long timeMillis = System.currentTimeMillis() - timestamp;

                    if (timeMillis < HttpServiceProps.SECOND) {
                        try {
                            Thread.sleep(HttpServiceProps.SECOND - timeMillis);

                        } catch (InterruptedException ex) {
                            throw new RuntimeException("기타 오류");
                        }
                    }

                    timestamp = System.currentTimeMillis();
                }
                count++;
            }

            executor.shutdown();
        }

        String[] result;
        String regId = "";
        String code = "";

        for (Future<String> future : resultList) {
            try {
                result = future.get().split("\\|");
                regId = result[0];
                code = result[1];

            } catch (Exception ex) {
                throw new RuntimeException("기타 오류");
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

}
