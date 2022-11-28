package com.lguplus.fleta.config.datasource;

import com.lguplus.fleta.config.datasource.property.EachDataSourceProperties;
import com.lguplus.fleta.config.datasource.property.HikariDataSourceProperties;
import com.lguplus.fleta.config.datasource.property.TargetDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TargetDataSourceFactory {

    private static final String POOL_NAME_READER = "readerPool";
    private static final String POOL_NAME_WRITER = "writerPool";

    private final TargetDataSourceProperties targetDataSourceProp;
    private final HikariDataSourceProperties hikariDataSourceProp;

    public DataSource createWriter() {
        return createDataSource(targetDataSourceProp.getWriter(), POOL_NAME_WRITER);
    }

    public DataSource createReader() {
        EachDataSourceProperties readerProp = targetDataSourceProp.getReader();
        if (!readerProp.isValid()) {
            readerProp.setBasicPropertiesFrom(targetDataSourceProp.getWriter());
        }

        HikariDataSource hds = createDataSource(readerProp, POOL_NAME_READER);
        hds.setReadOnly(true);
        return hds;
    }

    private HikariDataSource createDataSource(EachDataSourceProperties dataSourceProp, String poolName) {
        HikariDataSource hds = DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .username(dataSourceProp.getUsername())
            .password(dataSourceProp.getPassword())
            .build();

        hds.setJdbcUrl(dataSourceProp.getJdbcUrl());
        hds.setMinimumIdle(dataSourceProp.getMinimumIdle());
        hds.setMaximumPoolSize(dataSourceProp.getMaximumPoolSize());
        hds.setPoolName(poolName);

        hds.setConnectionTimeout(hikariDataSourceProp.getConnectionTimeout());
        hds.setValidationTimeout(hikariDataSourceProp.getValidationTimeout());
        hds.setLeakDetectionThreshold(hikariDataSourceProp.getLeakDetectionThreshold());
        hds.setMaxLifetime(hikariDataSourceProp.getMaxLifetime());
        hds.setDataSourceProperties(hikariDataSourceProp.getDataSourceProperties());
        return hds;
    }
}
