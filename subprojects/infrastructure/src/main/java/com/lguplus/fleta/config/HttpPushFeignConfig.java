package com.lguplus.fleta.config;

import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.provider.rest.HttpPushErrorDecoder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

public class HttpPushFeignConfig {

    @Value("${push.openapi.common.server.contenttype}")
    private String contentType;

    @Value("${push.openapi.common..server.accept}")
    private String accept;

    @Value("${push.openapi.common..server.encoding}")
    private String encoding;

    @Value("${push.openapi.single..server.auth}")
    private String authorizationSingle;

    @Value("${push.openapi.announce.server.auth}")
    private String authorizationAnnounce;

    @Bean
    public ErrorDecoder errorDecoder(HttpServiceProps httpServiceProps) {
        return new HttpPushErrorDecoder(httpServiceProps);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.ACCEPT, accept);
            requestTemplate.header(HttpHeaders.ACCEPT_CHARSET, encoding);
            requestTemplate.header(HttpHeaders.CONTENT_TYPE, contentType);
            requestTemplate.header(HttpHeaders.CONTENT_ENCODING, encoding);

            if (requestTemplate.url().endsWith("servicekey")) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationSingle);

            } else if (requestTemplate.url().endsWith("announce")) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationAnnounce);
            }
        };
    }

    @Bean
    @Profile({"local"})
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;//NONE, BASIC, HEADERS, FULL
    }

}
