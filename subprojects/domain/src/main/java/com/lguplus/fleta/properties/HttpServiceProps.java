package com.lguplus.fleta.properties;

import com.lguplus.fleta.config.HttpPushConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "http.service")
public class HttpServiceProps {

    private final HttpPushConfig.HttpPushExceptionCode httpPushExceptionCode;

    private final HttpPushConfig.HttpPushExceptionMessage httpPushExceptionMessage;

    /** 트랜잭션 ID 관리용(단건, 멀티) */
    public static AtomicInteger singleTransactionIdNum = new AtomicInteger(0);

    /** 트랜잭션 ID 관리용(공지) */
    public static AtomicInteger announceTransactionIdNum = new AtomicInteger(0);

    /** Push 요청 서버 타입 (단건, 멀티에서 사용) */
    public static final String PUSH_REQUEST_PART = "SP";

    public static final int SECOND = 1000;

    /** Push 요청 서버 타입(공지) */
    public static final String ANNOUNCE_REQUEST_PART = "WEB";

    /** GCM 만 사용하며, 한번에 GCM 으로 보내지는 Push notification request 의 registration_ids 개수 */
    public static final String GCM_MULTI_COUNT = "300";


    /** http push service-keys */
    private List<Map<String, String>> keys = null;


    /**
     * serviceId 에 해당하는 객체를 찾는다.
     *
     * @param serviceId 서비스아이디
     * @return serviceId 에 해당하는 객체
     */
    public Optional<Map<String, String>> findMapByServiceId(String serviceId) {
        return keys.stream().filter(m -> m.get("service_id").equals(serviceId)).findFirst();
    }

    /**
     * exceptionClassName 에 해당하는 code, message 를 가져온다.
     *
     * @param exceptionClassName exception 이 발생한 class 명
     * @return exceptionClassName 에 해당하는 code, message
     */
    public Pair<String, String> getExceptionCodeMessage(String exceptionClassName) {
        String exceptionCode = httpPushExceptionCode.getHttppush().get(exceptionClassName);

        return Pair.of(exceptionCode, httpPushExceptionMessage.getMessage().get(exceptionCode));
    }

}
