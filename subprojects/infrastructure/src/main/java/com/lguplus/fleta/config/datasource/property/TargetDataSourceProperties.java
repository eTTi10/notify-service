package com.lguplus.fleta.config.datasource.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.datasource")
@Setter
@Getter
public class TargetDataSourceProperties {

    @NestedConfigurationProperty
    private EachDataSourceProperties writer;

    @NestedConfigurationProperty
    private EachDataSourceProperties reader;

}
