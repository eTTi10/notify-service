package com.lguplus.fleta.config;

import com.lguplus.fleta.provider.rest.HttpPushErrorDecoder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

public class HttpPushFeignConfig {

    @Value("${singlepush.server.contenttype}")
    private String contentTypeSingle;

    @Value("${singlepush.server.accept}")
    private String acceptSingle;

    @Value("${singlepush.server.encoding}")
    private String encodingSingle;

    @Value("${singlepush.server.auth}")
    private String authorizationSingle;

    @Value("${announce.server.auth}")
    private String authorizationAnnounce;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new HttpPushErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.ACCEPT, acceptSingle);
            requestTemplate.header(HttpHeaders.ACCEPT_CHARSET, encodingSingle);
            requestTemplate.header(HttpHeaders.CONTENT_TYPE, contentTypeSingle);
            requestTemplate.header(HttpHeaders.CONTENT_ENCODING, encodingSingle);
            requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationSingle);

            /*if (requestTemplate.url().endsWith("servicekey")) {
                System.out.println("servicekey autho :::::::::: " + authorizationSingle);
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationSingle);

            } else if (requestTemplate.url().endsWith("announce")) {
                System.out.println("announce autho :::::::::: " + authorizationAnnounce);
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationAnnounce);

            }*/
        };
    }

    @Bean
    @Profile({"local"})
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;//NONE, BASIC, HEADERS, FULL
    }

}
