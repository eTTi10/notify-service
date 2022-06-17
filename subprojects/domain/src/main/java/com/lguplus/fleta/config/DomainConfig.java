package com.lguplus.fleta.config;

import com.lguplus.fleta.data.type.CacheName;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan(basePackages = "com.lguplus.fleta")
public class DomainConfig {

    @PostConstruct
    void validateCacheNames() throws ClassNotFoundException {
        Class.forName(CacheName.class.getName());
    }
}
