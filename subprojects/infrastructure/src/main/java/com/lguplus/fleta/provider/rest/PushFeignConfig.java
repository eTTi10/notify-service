package com.lguplus.fleta.provider.rest;

import feign.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class PushFeignConfig {

    @Value("${push-comm.announce.server.header}")
    private String contentType;

    @Value("${push-comm.announce.server.encoding}")
    private String contentEncoding;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //Header
                template.header("Content-Type", contentType);
                template.header("Accept", contentType);
                template.header("Accept-Charset", contentEncoding);
                template.header("Content-Encoding", contentEncoding);
                //template.header("header_1", "value_1");
                //requestTemplate.header("Authorization", String.format("%s %s", "auth", "========auth_key============"));
            }
        };
    }

    @Bean
    public static Request.Options requestOptions(ConfigurableEnvironment env) {
       // String setTimeout = timeOut.replace("'", "");
        //log.debug("requestOptions: {}", timeOut);
        long socketTimeout = 2000;
        long readTimeout = 2000;

        return new Request.Options(socketTimeout, TimeUnit.MILLISECONDS
                , readTimeout, TimeUnit.MILLISECONDS
                , true);
    }

}
