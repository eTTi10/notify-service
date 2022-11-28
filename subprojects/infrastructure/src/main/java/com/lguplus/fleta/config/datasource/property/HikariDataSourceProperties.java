package com.lguplus.fleta.config.datasource.property;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.datasource.hikari")
@Setter
@Getter
public class HikariDataSourceProperties {

    private Long connectionTimeout;
    private Long validationTimeout;
    private Long leakDetectionThreshold;
    private Long maxLifetime;
    private Properties dataSourceProperties;
}
