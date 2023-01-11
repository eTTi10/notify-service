package com.lguplus.fleta.config;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("tst")
@EnableAutoConfiguration(exclude = RedissonAutoConfiguration.class)
@Configuration
public class RedissonExcludeConfig {

}
