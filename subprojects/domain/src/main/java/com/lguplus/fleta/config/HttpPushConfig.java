package com.lguplus.fleta.config;

import com.lguplus.fleta.util.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpPush 설정 파일
 */
@Configuration
@PropertySource(value = "classpath:httppush/httppush-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:httppush/httpservice-${spring.profiles.active}.yml", factory = YamlPropertySourceFactory.class)
public class HttpPushConfig {

    @Bean
    @ConfigurationProperties(prefix = "error.flag.com.lguplus.fleta.exception")
    public HttpPushExceptionCode httpPushExcetionCode() {
        return new HttpPushExceptionCode();
    }

    public static class HttpPushExceptionCode {
        private Map<String, String> httppush = new HashMap<>();

        public Map<String, String> getHttppush() {
            return httppush;
        }

        public void setHttppush(Map<String, String> httppush) {
            this.httppush = httppush;
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "error")
    public HttpPushExceptionMessage httpPushExceptionMessage() {
        return new HttpPushExceptionMessage();
    }

    public static class HttpPushExceptionMessage {
        private Map<String, String> message = new HashMap<>();

        public Map<String, String> getMessage() {
            return message;
        }

        public void setMessage(Map<String, String> message) {
            this.message = message;
        }
    }

}
