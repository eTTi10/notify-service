package com.lguplus.fleta.config;

import com.lguplus.fleta.provider.rest.HttpPushErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class HttpPushFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new HttpPushErrorDecoder();
    }

    @Bean
    @Profile({"local"})
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;//NONE, BASIC, HEADERS, FULL
    }

}
