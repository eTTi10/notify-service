package com.lguplus.fleta.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ComponentScan(basePackages = "com.lguplus.fleta")
@EnableTransactionManagement
public class InfrastructureConfig {

    private static final String WDS = "writerDataSource";
    private static final String RDS = "readerDataSource";

    private final HikariConfigProperties hikariConfigProperties;

    @Bean(WDS)
    public DataSource writerDataSource() {
        log.debug(">>> hikariConfigProperties: {}", this.hikariConfigProperties.toString());
        return this.hikariConfigProperties.getWriterDataSource();
    }

    @Bean(RDS)
    public DataSource readDataSource() {
        return this.hikariConfigProperties.getReaderDataSource();
    }

    @Bean
    @DependsOn({ WDS, RDS })
    public DataSource routingDataSource(@Qualifier(WDS) DataSource writerDataSource, @Qualifier(RDS) DataSource readDataSource) {
        Map<Object, Object> datasourceMap = new HashMap<>();
        datasourceMap.put("writer", writerDataSource);
        datasourceMap.put("reader", readDataSource);

        CustomRoutingDataSource routingDataSource = new CustomRoutingDataSource();
        routingDataSource.setTargetDataSources(datasourceMap);
        routingDataSource.setDefaultTargetDataSource(writerDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean
    @DependsOn("routingDataSource")
    public DataSource lazyConnectionDataSource(DataSource routingDataSource){
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("lazyConnectionDataSource") DataSource lazyConnectionDataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(lazyConnectionDataSource);
        return transactionManager;
    }
}
