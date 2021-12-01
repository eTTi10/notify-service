package com.lguplus.fleta.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.data.dto.request.inner.HttpPushDto;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import com.lguplus.fleta.properties.HttpServiceProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 *  Http Push 관련 유틸 클래스
 *
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HttpPushSupport {

    private final HttpServiceProps httpServiceProps;

    /**
     * transactionId 를 가져온다.
     *
     * @param transactionNum 4자리수 넘지 않는 방어코드 숫자
     * @return 생성된 transactionId
     */
    private String getTransactionId(int transactionNum) {
        NumberFormat nf = new DecimalFormat("0000");
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + nf.format(transactionNum);
    }

    /**
     * SHA512 암호화
     *
     * @param password 암호화 할 값
     * @return 암호화된 값
     */
    private String encryptServicePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(password.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
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

    /**
     * 요청시간을 생성해 가져온다.
     *
     * @return 생성된 요청시간
     */
    private String getRequestTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * serviceId 에 해당되는 servicePwd 를 암호화해서 가져온다.
     *
     * @param serviceId 서비스ID
     * @return 암호화된 servicePwd
     */
    private String getEncryptedServicePassword(String serviceId) {
        //서비스 KEY
        Map<String, String> serviceMap = httpServiceProps.findMapByServiceId(serviceId).orElseThrow(ServiceIdNotFoundException::new);    // 1115 서비스ID 확인 불가;

        String servicePwd = Optional.of(serviceMap.get("service_pwd")).orElseThrow(ServiceIdNotFoundException::new);    // 1115 서비스ID 확인 불가;

        log.debug("service_id ::::::::::::::: {}\tservice_pwd ::::::::::::: {}", serviceId, servicePwd);

        // service_pwd : SHA512 암호화
        servicePwd = encryptServicePassword(servicePwd);

//        log.debug("encrypted service_pwd :::::::::::::::::::::: {}", servicePwd);

        return servicePwd;
    }

    /**
     * 푸시(단건, 멀티) Open API 를 호춣할 파라미터를 만든다.
     *
     * @param appId 어플리케이션 ID
     * @param serviceId 서비스 등록시 부여받은 Unique ID
     * @param pushType Push 발송 타입 (G: 안드로이드, A: 아이폰)
     * @param msg 보낼 메시지
     * @param regId 사용자 ID
     * @param items 추가할 항목 입력(name!^value)
     * @return 푸시(단건, 멀티) Open API 를 호춣할 생성된 파라미터
     */
    public Map<String, Object> makePushParameters(String appId, String serviceId, String pushType, String msg, String regId, List<String> items) {
//        log.debug("before msg ::::::::::::::::::::::::::::::: {}", msg);

        String servicePwd = getEncryptedServicePassword(serviceId);

        // 4자리수 넘지 않도록 방어코드
        if (HttpServiceProps.singleTransactionIDNum.get() >= 9999) {
            HttpServiceProps.singleTransactionIDNum.set(0);
        }

//        log.debug("transactionDate :::::::: {}", transactionDate);

        int transactionNum = HttpServiceProps.singleTransactionIDNum.incrementAndGet();

        String transactionId = getTransactionId(transactionNum);

        log.debug("transactionId :::::::: {}", transactionId);

        // PAYLOAD
        String payload = "";

        // 안드로이드
        if (pushType.equals("G")) {
            Function<String, String> gcmPushOpenApiPayload = m -> "{" + replacePayload(msg) + "}";

            payload = gcmPushOpenApiPayload.apply(msg);

        // 아이폰("A")
        } else {
            Function<Pair<String, List<String>>, String> apnOpenApiPayload = apnPayload(Pair.of(msg, items));

            payload = apnOpenApiPayload.apply(Pair.of(msg, items));
        }

//        log.debug("after msg ::::::::::::::::::::::::::::::: {}", payload);

        HttpPushDto httpPushDto = HttpPushDto.builder()
                .requestPart(HttpServiceProps.PUSH_REQUEST_PART)
                .requestTime(getRequestTime())
                .pushId(transactionId)
                .serviceId(serviceId)
                .servicePass(servicePwd)
                .applicationId(appId)
                .serviceKey(regId)
                .payload(payload)
                .build();

//        log.debug("HttpPushDto ::::::::::::::::::::::::::: {}", httpPushDto);

        return makePushMap(httpPushDto, "S", "");
    }

    /**
     * 공지 Open API 를 호춣할 파라미터를 만든다.
     *
     * @param appId 어플리케이션 ID
     * @param serviceId 서비스 등록시 부여받은 Unique ID
     * @param pushType Push 발송 타입 (G: 안드로이드, A: 아이폰)
     * @param msg 보낼 메시지
     * @param items 추가할 항목 입력(name!^value)
     * @return 공지 Open API 를 호춣할 생성된 파라미터
     */
    public Map<String, Object> makeAnnounceParameters(String appId, String serviceId, String pushType, String msg, List<String> items) {
//        log.debug("before msg ::::::::::::::::::::::::::::::: {}", msg);

        String servicePwd = getEncryptedServicePassword(serviceId);

        // 4자리수 넘지 않도록 방어코드
        if (HttpServiceProps.announceTransactionIDNum.get() >= 9999) {
            HttpServiceProps.announceTransactionIDNum.set(0);
        }

//        log.debug("transactionDate :::::::: {}", transactionDate);

        int transactionNum = HttpServiceProps.announceTransactionIDNum.incrementAndGet();

        String transactionId = getTransactionId(transactionNum);

        log.debug("transactionId :::::::: {}", transactionId);

        // PAYLOAD
        String payload = "";
        String gcmMultiCount = HttpServiceProps.GCM_MULTI_COUNT;

        // 안드로이드
        if (pushType.equals("G")) {
            Function<String, Pair<String, String>> gcmAnnounceOpenApiPayload = m -> {
                String[] config;
                String tmpGcmMultiCount = "";

                for (String item : items) {
                    config = item.split("!\\^");

                    if (config.length >= 2) {
                        if (config[0].equalsIgnoreCase("gcm_multi_count")) {
                            tmpGcmMultiCount = config[1];
                        }
                    }
                }

                return Pair.of("{" + replacePayload(msg) + "}", tmpGcmMultiCount);
            };

            Pair<String, String> tmpPair = gcmAnnounceOpenApiPayload.apply(msg);
            payload = tmpPair.getLeft();
            gcmMultiCount = tmpPair.getRight();

        // 아이폰("A")
        } else {
            Function<Pair<String, List<String>>, String> apnOpenApiPayload = apnPayload(Pair.of(msg, items));

            payload = apnOpenApiPayload.apply(Pair.of(msg, items));
        }

//        log.debug("after msg ::::::::::::::::::::::::::::::: {}", payload);

        HttpPushDto httpPushDto = HttpPushDto.builder()
                .requestPart(HttpServiceProps.ANNOUNCE_REQUEST_PART)
                .requestTime(getRequestTime())
                .pushId(transactionId)
                .serviceId(serviceId)
                .servicePass(servicePwd)
                .applicationId(appId)
                .payload(payload)
                .gcmMultiCount(gcmMultiCount)
                .build();

//        log.debug("HttpPushDto ::::::::::::::::::::::::::: {}", httpPushDto);

        return makePushMap(httpPushDto, "A", pushType);
    }

    /**
     * Open API 를 호출하기 위한 파라미터를 만든다.
     *
     * @param httpPushDto HttpPush 관련 DTO
     * @param kind 푸시 종류 [S:(단건, 멀티), A:공지]
     * @param pushType Push 발송 타입 (G: 안드로이드, A: 아이폰)
     * @return 생성된 파라미터
     */
    private Map<String, Object> makePushMap(HttpPushDto httpPushDto, String kind, String pushType) {
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("REQUEST_PART", httpPushDto.getRequestPart());
        pushMap.put("REQUEST_TIME", httpPushDto.getRequestTime());
        pushMap.put("PUSH_ID", httpPushDto.getPushId());
        pushMap.put("SERVICE_ID", httpPushDto.getServiceId());
        pushMap.put("SERVICE_PASS", httpPushDto.getServicePass());
        pushMap.put("APPLICATION_ID", httpPushDto.getApplicationId());
        try {
            pushMap.put("PAYLOAD", new ObjectMapper().readValue(httpPushDto.getPayload(), new TypeReference<Object>(){}));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("기타 오류");
        }

        // 단건, 멀티
        if (kind.equals("S")) {
            pushMap.put("SERVICE_KEY", httpPushDto.getServiceKey());
            pushMap.put("SUB_SERVICE_ID", httpPushDto.getSubServiceId());

        // 공지
        } else {
            // GCM(안드로이드)
            if (pushType.equals("G")) {
                pushMap.put("GCM_MULTI_COUNT", httpPushDto.getGcmMultiCount());
            }
        }

        return pushMap;
    }

    /**
     * APN(아이폰) 관련 Payload 를 생성해서 Function interface 를 리턴한다.
     *
     * @param paramPair 메시지와 옵션들이 들어있는 Pair 변수
     * @return APN(아이폰) 관련 Payload 를 생성된  Function interface
     */
    public Function<Pair<String, List<String>>, String> apnPayload(Pair<String, List<String>> paramPair) {
        return m -> {
            String msg = paramPair.getLeft();
            List<String> items = paramPair.getRight();
            StringBuilder sb = new StringBuilder();
            String[] config;
            String tmpCm = "";

            sb.append("{");
            sb.append("\"aps\":{");
            sb.append("\"alert\":{");
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

            return sb.toString();
        };
    }

}
