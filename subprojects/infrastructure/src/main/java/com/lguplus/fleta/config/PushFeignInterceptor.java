package com.lguplus.fleta.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PushFeignInterceptor implements RequestInterceptor {

    @Value("${push.gateway.announce.server.header}")
    private String contentType;

    @Value("${push.gateway.announce.server.encoding}")
    private String contentEncoding;

    @Override
    public void apply(RequestTemplate template) {
        log.debug("PushFeignConfig apply==============================> {},{}", contentType, contentEncoding);
        template.header("Content-Type", contentType);
        template.header("Accept", contentType);
        template.header("Accept-Charset", contentEncoding);
        template.header("Content-Encoding", contentEncoding);
    }
}
