package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Latest최신회 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:latest/latest-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class LatestConfig {
}
