package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

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

    public PushConfig(final StandardEnvironment environment) {

        final PropertiesPropertySource propertySource = (PropertiesPropertySource)
                environment.getPropertySources().get("push_config");
        if (propertySource == null) {
            throw new IllegalStateException("Error properties file not found.");
        }

        AtomicInteger cnt1= new AtomicInteger();
        AtomicInteger cnt2= new AtomicInteger();

        propertySource.getSource()
                .forEach((propertyName, propertyValue) -> {
                    if (propertyName.startsWith(PUSH_COMM_PROPERTY_PREFIX)) {
                        cnt1.getAndIncrement();
                        String nm = propertyName.replace(PUSH_COMM_PROPERTY_PREFIX, "");
                        propertiesPushComm.put(nm, String.valueOf(propertyValue));
                    } else if (propertyName.startsWith(PUSH_SERVICE_PROPERTY_PREFIX)) {
                        cnt2.getAndIncrement();
                        String nm = propertyName.replace(PUSH_SERVICE_PROPERTY_PREFIX, "");
                        propertiesPushService.put(nm, String.valueOf(propertyValue));
                    }
                });

        log.debug("########### Properties: ", cnt1, cnt2);
    }

    public String getCommPropValue(String key) {
        if(!propertiesPushComm.containsKey(key)) {
            //throw new Ex
        }
        return propertiesPushComm.get(key);
    }

    public String getServicePropValue(String key) {
        if(!propertiesPushService.containsKey(key)) {
            //throw new Ex
        }
        return propertiesPushService.get(key);
    }
}
