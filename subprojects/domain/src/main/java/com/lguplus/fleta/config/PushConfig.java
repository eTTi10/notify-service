package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpPush 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:push/push-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class PushConfig {
}
