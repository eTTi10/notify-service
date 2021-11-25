package com.lguplus.fleta.config;

import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import com.lguplus.fleta.util.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpPush 설정 파일
 */
@Slf4j
@Configuration
@Component
@PropertySource(name = "push_config", value = "classpath:push/push-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class PushConfig {

    private static final String PUSH_COMM_PROPERTY_PREFIX = "push-comm.";
    private static final String PUSH_SERVICE_PROPERTY_PREFIX = "push-service.";

    private final Map<String, String> propertiesPushComm = new HashMap<>();
    private final Map<String, String> propertiesPushService = new HashMap<>();
    private final Map<String, String> propertiesPushServiceLinkType = new HashMap<>();

    public PushConfig(final StandardEnvironment environment) {

        final PropertiesPropertySource propertySource = (PropertiesPropertySource)
                environment.getPropertySources().get("push_config");
        if (propertySource == null) {
            throw new IllegalStateException("Error properties file not found.");
        }

        propertySource.getSource()
                .forEach((propertyName, propertyValue) -> {
                    if (propertyName.startsWith(PUSH_COMM_PROPERTY_PREFIX)) {
                        String nm = propertyName.replace(PUSH_COMM_PROPERTY_PREFIX, "");
                        propertiesPushComm.put(nm, String.valueOf(propertyValue));
                    } else if (propertyName.startsWith(PUSH_SERVICE_PROPERTY_PREFIX) && propertyName.endsWith(".service_id")) {
                        String nm = propertyName.replace(PUSH_SERVICE_PROPERTY_PREFIX, "");
                        String serviceId = String.valueOf(propertyValue);
                        String servicePass = String.valueOf(propertySource.getSource().get(propertyName.replace(".service_id", ".service_pwd")));
                        propertiesPushService.put(serviceId, getSha512Pwd(servicePass));

                        if(propertySource.getSource().containsKey(propertyName.replace(".service_id", ".linkage_type"))) {
                            String linkType = String.valueOf(propertySource.getSource().get(propertyName.replace(".service_id", ".linkage_type")));
                            propertiesPushServiceLinkType.put(serviceId, linkType);
                        }
                    }
                });
    }

    public String getCommPropValue(String key) {
        return propertiesPushComm.get(key);
    }

    public String getServicePassword(String serviceId) {
        return propertiesPushService.get(serviceId);
    }

    public String getServiceLinkType(String serviceId) {
        return propertiesPushServiceLinkType.get(serviceId);
    }

    // Service Password
    private String getSha512Pwd(String servicePwd) {
        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
    }
}
