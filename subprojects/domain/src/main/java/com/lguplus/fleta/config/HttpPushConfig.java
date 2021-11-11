package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * HttpPush 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:httppush-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:httpservice-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class HttpPushConfig {

}
