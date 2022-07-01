package com.lguplus.fleta.config;

import com.lguplus.fleta.data.type.CacheName;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.lguplus.fleta")
public class DomainConfig {

    @PostConstruct
    void validateCacheNames() throws ClassNotFoundException {
        Class.forName(CacheName.class.getName());
    }
}
