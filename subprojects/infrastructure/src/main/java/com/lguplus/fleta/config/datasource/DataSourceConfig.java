package com.lguplus.fleta.config.datasource;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private static final String WDS = "writerDataSource";
    private static final String RDS = "readerDataSource";

    private final HikariConfigProperties hikariConfigProperties;

    @Bean(WDS)
    public DataSource writerDataSource() {
        return this.hikariConfigProperties.getWriterDataSource();
    }

    @Bean(RDS)
    public DataSource readDataSource() {
        return this.hikariConfigProperties.getReaderDataSource();
    }

    @Bean
    @DependsOn({WDS, RDS})
    public DataSource routingDataSource(@Qualifier(WDS) DataSource writerDataSource, @Qualifier(RDS) DataSource readDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(TargetDataSource.WRITE, writerDataSource);
        targetDataSources.put(TargetDataSource.READ, readDataSource);

        CustomRoutingDataSource routingDataSource = new CustomRoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writerDataSource);
        return routingDataSource;
    }

    @Primary
    @Bean
    @DependsOn("routingDataSource")
    public DataSource lazyConnectionDataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("lazyConnectionDataSource") DataSource lazyConnectionDataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(lazyConnectionDataSource);
        return transactionManager;
    }

    enum TargetDataSource {
        WRITE, READ
    }

    @Slf4j
    static class CustomRoutingDataSource extends AbstractRoutingDataSource {

        @Override
        protected Object determineCurrentLookupKey() {
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.debug(">>> readOnly: {}", readOnly);
            return readOnly ? TargetDataSource.READ : TargetDataSource.WRITE;
        }
    }
}
