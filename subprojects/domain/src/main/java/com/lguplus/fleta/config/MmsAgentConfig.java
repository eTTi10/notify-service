package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MMSAgent 설정 파일
 */
@Configuration
//@PropertySource(value = "classpath:mmsagent/mmsagent-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:mmsagent/mmsagent-test.yml", factory = YamlPropertySourceFactory.class)
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "mmsroot")
@Getter
@Setter
public class MmsAgentConfig {
    private Map<String, ?> mms;
}
