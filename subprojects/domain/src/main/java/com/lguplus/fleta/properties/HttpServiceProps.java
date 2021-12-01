package com.lguplus.fleta.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "http.service")
public class HttpServiceProps {

    /** 트랜잭션 ID 관리용(단건, 멀티) */
    public static AtomicInteger singleTransactionIDNum = new AtomicInteger(0);

    /** 트랜잭션 ID 관리용(공지) */
    public static AtomicInteger announceTransactionIDNum = new AtomicInteger(0);

    /** Push 요청 서버 타입 (단건, 멀티에서 사용) */
    public static final String PUSH_REQUEST_PART = "SP";

    public static final int SECOND = 1000;

    /** Push 요청 서버 타입(공지) */
    public static final String ANNOUNCE_REQUEST_PART = "WEB";

    /** GCM 만 사용하며, 한번에 GCM 으로 보내지는 Push notification request 의 registration_ids 개수 */
    public static final String GCM_MULTI_COUNT = "300";


    /** http push service-keys */
   private List<Map<String, String>> keys;


    /**
     * serviceId 에 해당하는 객체를 찾는다.
     *
     * @param serviceId 서비스아이디
     * @return serviceId 에 해당하는 객체
     */
   public Optional<Map<String, String>> findMapByServiceId(String serviceId) {
       return keys.stream().filter(m -> m.get("service_id").equals(serviceId)).findFirst();
   }

}
