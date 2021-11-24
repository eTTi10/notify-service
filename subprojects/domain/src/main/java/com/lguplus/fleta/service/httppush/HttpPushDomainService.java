package com.lguplus.fleta.service.httppush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Http Push Component
 *
 * 단건, 멀티, 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpServiceProps httpServiceProps;

//    private final ObjectMapper objectMapper;

    private static final String REQUEST_PART = "SP";    // Push 요청 서버 타입 (단건, 멀티에서 사용)

    private static final int SECOND = 1000;

    private final String lock = "";

    @Value("${multi.push.max.tps}")
    private String maxMultiCount;

    @Value("${multi.push.reject.regList}")
    private String exception;

    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        log.debug("httpPushSingleRequestDto ::::::::::::::: {}", httpPushSingleRequestDto);

//        httpServiceProps.getKeys().forEach(m -> log.debug(m.toString()));

        // 발송 제외 가번 확인
        log.debug("exception :::::::::::::::::::: {}", exception);
        String[] exceptionList = exception.split("\\|");
        String regId = httpPushSingleRequestDto.getUsers().get(0);

        if (Arrays.asList(exceptionList).contains(regId.strip())) {
            throw new ExclusionNumberException("발송제한번호");   // 9998
        }

        String appId = httpPushSingleRequestDto.getAppId();
        String serviceId = httpPushSingleRequestDto.getServiceId();
        String pushType = httpPushSingleRequestDto.getPushType();
        String msg = httpPushSingleRequestDto.getMsg();
        List<String> items = httpPushSingleRequestDto.getItems();

        OpenApiPushResponseDto openApiPushResponseDto = requestHttpPush(appId, serviceId, pushType, msg, regId, items);

        // 성공
        if (openApiPushResponseDto.getReturnCode().equals("200")) {
            return HttpPushResponseDto.builder().build();
        }

        // 실패
        return HttpPushResponseDto.builder()
                .code(openApiPushResponseDto.getReturnCode())
                .message(openApiPushResponseDto.getError().get("MESSAGE"))
                .build();
    }

    /**
     * 멀티푸시등록(단건푸시 사용)
     *
     * @param httpPushMultiRequestDto 멀티푸시등록을 위한 DTO
     * @return 멀티푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushMulti(HttpPushMultiRequestDto httpPushMultiRequestDto) {
        log.debug("httpPushMultiRequestDto ::::::::::::::: {}", httpPushMultiRequestDto);

//        httpServiceProps.getKeys().forEach(m -> log.debug(m.toString()));

        log.debug("before maxMultiCount :::::::::::: {}", maxMultiCount);

        // 초당 최대 Push 전송 허용 갯수
        Integer maxLimitPush = Integer.parseInt(maxMultiCount);

        log.debug("after maxMultiCount :::::::::::: {}", maxLimitPush);

        if (httpPushMultiRequestDto.getMultiCount() > maxLimitPush || httpPushMultiRequestDto.getMultiCount() == 0) {
            httpPushMultiRequestDto.setMultiCount(maxLimitPush);
        }

        maxLimitPush = httpPushMultiRequestDto.getMultiCount();

        List<String> successUsers = new ArrayList<>();
        List<String> failUsers = new ArrayList<>();
        List<Future<String>> resultList = new ArrayList<>();

        // Push GW 제한 성능 문제로 동시 발송을 허용하지 않음.
        synchronized (lock) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            int count = 1;
            long timestamp = System.currentTimeMillis();
            String[] exceptionList = exception.split("\\|");

            // Push 메시지 전송
            String appId = httpPushMultiRequestDto.getAppId();
            String serviceId = httpPushMultiRequestDto.getServiceId();
            String pushType = httpPushMultiRequestDto.getPushType();
            String msg = httpPushMultiRequestDto.getMsg();
            List<String> items = httpPushMultiRequestDto.getItems();

            List<String> users = httpPushMultiRequestDto.getUsers();

            for (String regId : users) {
//                log.debug("exception :::::::::::::::::::: {}", exception);

                // 사용자별 필수 값 체크 & 발송 제외 가번 확인
                if (Arrays.asList(exceptionList).contains(regId.strip())) {
                    continue;
                }

                resultList.add(executor.submit(() -> {
                            try {
                                return regId + "|" + requestHttpPush(appId, serviceId, pushType, msg, regId, items).getError().get("CODE");

                            } catch (Exception ex) {
                                return regId + "|" + "900";
                            }
                        })
                );

                // TPS 설정에 따른 Time delay
                if (count % maxLimitPush == 0) {
                    long timeMillis = System.currentTimeMillis() - timestamp;

                    if (timeMillis < SECOND) {
                        try {
                            Thread.sleep(SECOND - timeMillis);

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

            // Push 메시지 전송 실패
            if (!code.equals("200")) {
                log.debug("Push 메시지 전송 실패 code ::::::::::::::::::: {}", code);

                // code "1112", message "The request Accepted"
                if (code.equals("202")) {
                    throw new AcceptedException();

                // code "1104", message "Push GW BZadRequest"
                } else if (code.equals("400")) {
                    throw new BadRequestException();

                // code "1105", message "Push GW UnAuthorized"
                } else if (code.equals("401")) {
                    throw new UnAuthorizedException();

                // code "1106", message "Push GW Forbidden"
                } else if (code.equals("403")) {
                    throw new ForbiddenException();

                // code "1107", message "Push GW Not Found"
                } else if (code.equals("404")) {
                    throw new NotFoundException();

                // 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip함
                } else if (code.equals("410") || code.equals("412")) {
                    log.debug("유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip함");

                // 메시지 전송 실패 - Retry 대상
                } else {
                    log.debug("메시지 전송 실패 - Retry 대상");
                    failUsers.add(regId);
                }
            }
        }

        // 성공
        if (failUsers.isEmpty()) {
            return HttpPushResponseDto.builder().build();
        }

        // 메시지 전송이 한건이라도 실패한 경우
        return HttpPushResponseDto.builder()
                .code("1130")
                .message("메시지 전송 실패")
                .failUsers(failUsers)
                .build();
    }

    /**
     * Open API 를 호춣한다.
     *
     * @param appId 어플리케이션 ID
     * @param serviceId 서비스 등록시 부여받은 Unique ID
     * @param pushType Push발송 타입 (G: 안드로이드, A: 아이폰)
     * @param msg 보낼 메시지
     * @param regId 사용자 ID
     * @param items 추가할 항목 입력(name!^value)
     * @return Open API 를 호춣 결과 DTO
     */
    private OpenApiPushResponseDto requestHttpPush(String appId, String serviceId, String pushType, String msg, String regId, List<String> items) {
        log.debug("before msg ::::::::::::::::::::::::::::::: {}", msg);

        // 4자리수 넘지 않도록 방어코드
        if (HttpServiceProps.singleTransactionIDNum.get() >= 9999) {
            HttpServiceProps.singleTransactionIDNum.set(0);
        }

        String transactionDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

//        log.debug("transactionDate :::::::: {}", transactionDate);

        int transactionNum = HttpServiceProps.singleTransactionIDNum.incrementAndGet();

        NumberFormat nf = new DecimalFormat("0000");
        String transactionId = transactionDate + nf.format(transactionNum);

        log.debug("transactionId :::::::: {}", transactionId);

        //서비스 KEY
        Map<String, String> serviceMap = httpServiceProps.findMapByServiceId(serviceId).orElseThrow(() -> new ServiceIdNotFoundException("서비스ID 확인 불가"));    // 1115;

        String servicePwd = Optional.of(serviceMap.get("service_pwd")).orElseThrow(() -> new ServiceIdNotFoundException("서비스ID 확인 불가"));    // 1115;

        log.debug("service_id ::::::::::::::: {}\tservice_pwd ::::::::::::: {}", serviceId, servicePwd);

        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            servicePwd = String.format("%0128x", new BigInteger(1, digest.digest()));

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }

        log.debug("encrypted service_pwd :::::::::::::::::::::: {}", servicePwd);

        // PAYLOAD
        String payload = "";

        // 안드로이드
        if (pushType.equals("G")) {
//					sb.append("\"MSG1\":"+"\""+msg+"\",");
//					sb.append("\"PushCtrl\":"+"\"MSG\"");
            payload = "{" + replacePayload(msg) + "}";

        // 아이폰("A")
        } else {
            StringBuilder sb = new StringBuilder();
            String[] config;
            String tmpCm = "";

            sb.append("{");
            sb.append("\"aps\":{");
            sb.append("\"alert\":{");
//					sb.append("\"MESSAGE\":"+"\""+msg+"\"}");
            sb.append(replacePayload(msg)).append("}");

            for (String item : items) {
                config = item.split("!\\^");

                if (config.length >= 2) {
                    if (config[0].equalsIgnoreCase("cm")) {
                        tmpCm = ",\"" + config[0] + "\":\"" + config[1] + "\"";

                    } else {
                        sb.append(",\"").append(config[0]).append("\":\"").append(config[1]).append("\"");
                    }
                }
            }

            sb.append("}").append(tmpCm).append("}");

            payload = sb.toString();
        }

        log.debug("after msg ::::::::::::::::::::::::::::::: {}", payload);

        String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

//        log.debug("requestTime :::::::::::::::::::::::::: {}", requestTime);

        HttpPushDto httpPushDto = HttpPushDto.builder()
                .requestPart(REQUEST_PART)
                .requestTime(requestTime)
                .pushId(transactionId)
                .serviceId(serviceId)
                .servicePass(servicePwd)
                .applicationId(appId)
                .serviceKey(regId)
                .payload(payload)
                .build();

        log.debug("HttpPushDto ::::::::::::::::::::::::::: {}", httpPushDto);

        Map<String, String> pushMap = new HashMap<>();
        pushMap.put("REQUEST_PART", httpPushDto.getRequestPart());
        pushMap.put("REQUEST_TIME", httpPushDto.getRequestTime());
        pushMap.put("PUSH_ID", httpPushDto.getPushId());
        pushMap.put("SERVICE_ID", httpPushDto.getServiceId());
        pushMap.put("SERVICE_PASS", httpPushDto.getServicePass());
        pushMap.put("APPLICATION_ID", httpPushDto.getApplicationId());
        pushMap.put("SERVICE_KEY", httpPushDto.getServiceKey());
        pushMap.put("SUB_SERVICE_ID", httpPushDto.getSubServiceId());
        pushMap.put("PAYLOAD", httpPushDto.getPayload());

//        try {
//            String jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pushMap);
//
//            log.debug("jsonStr ::::::::::::::::::::::::::::::::: \n{}", jsonStr);
//
//        } catch (JsonProcessingException ex) {
//            throw new RuntimeException("기타 오류");
//        }

        OpenApiPushResponseDto openApiPushResponseDto = httpPushDomainClient.requestHttpPushSingle(pushMap);

        try {
            log.debug("openApiPushResponseDto :::::::::::::::::::: \n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(openApiPushResponseDto));

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        return openApiPushResponseDto;
    }

    /**
     * 필요없는 특수문자를 제거
     *
     * @param payload 입력 payload
     * @return 제거된 payload
     */
    private String replacePayload(String payload) {
        return payload.replace('\b', ' ').replace('\t', ' ').replace('\n', ' ').replace('\f', ' ').replace('\r', ' ').replace("\\\\\\\"", "&quot;").replace("\\", "").replace("&quot;", "\\\\\\\"");
    }

}
