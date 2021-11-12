package com.lguplus.fleta.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    /** 트랜잭션 ID관리용 */
    public static AtomicInteger singleTransactionIDNum = new AtomicInteger(0);

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
