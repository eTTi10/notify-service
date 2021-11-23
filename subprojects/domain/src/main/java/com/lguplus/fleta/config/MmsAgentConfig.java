package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * MMSAgent 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:mmsagent/mmsagent-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:mmsagent/agent-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class MmsAgentConfig {
}
