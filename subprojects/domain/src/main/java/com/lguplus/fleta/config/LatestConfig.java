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
 * Latest최신회 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:latest/latest.yml", factory = YamlPropertySourceFactory.class)
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "latestroot")
@Getter
@Setter
public class LatestConfig {
    private Map<String, ?> latest;
}
