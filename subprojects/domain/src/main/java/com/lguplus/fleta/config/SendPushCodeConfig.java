package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:sendpushcode/sendpushcode-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class SendPushCodeConfig {
}
