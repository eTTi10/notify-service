package com.lguplus.fleta.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = {"com.lguplus.fleta"})
@Configuration
public class FeignConfig {

}
