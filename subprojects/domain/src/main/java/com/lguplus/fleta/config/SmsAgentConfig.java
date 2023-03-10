package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:smsagent/smsagent-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class SmsAgentConfig {

}
