package com.lguplus.fleta.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@EnableFeignClients(basePackages = {"com.lguplus.fleta"})
@Configuration
public class FeignConfig {

    private ObjectFactory<HttpMessageConverters> getObjectFactory() {

        final ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final HttpMessageConverter<Object> messageConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        return () -> new HttpMessageConverters(messageConverter);
    }

    @Bean
    Encoder feignEncoder() {

        return new SpringEncoder(getObjectFactory());
    }

    @Bean
    Decoder feignDecoder() {

        return new ResponseEntityDecoder(new SpringDecoder(getObjectFactory()));
    }

}
