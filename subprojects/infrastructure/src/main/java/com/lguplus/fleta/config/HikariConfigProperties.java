package com.lguplus.fleta.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

@Setter
@ToString
@Component
@ConfigurationProperties("spring.datasource.hikari")
public class HikariConfigProperties {

    private Long connectionTimeout;
    private Long validationTimeout;
    private Long leakDetectionThreshold;
    private Properties dataSourceProperties;

    @Value("${spring.datasource.writer.jdbc-url}")
    private String writerJdbcUrl;
    @Value("${spring.datasource.writer.username}")
    private String writerUsername;
    @Value("${spring.datasource.writer.password}")
    private String writerPassword;
    @Value("${spring.datasource.writer.minimum-idle:10}")
    private Integer writerMinIdle;
    @Value("${spring.datasource.writer.maximum-pool-size:10}")
    private Integer writerMaxPoolSize;

    @Value("${spring.datasource.reader.jdbc-url}")
    private String readerJdbcUrl;
    @Value("${spring.datasource.reader.username}")
    private String readerUsername;
    @Value("${spring.datasource.reader.password}")
    private String readerPassword;
    @Value("${spring.datasource.reader.minimum-idle:10}")
    private Integer readerMinIdle;
    @Value("${spring.datasource.reader.maximum-pool-size:10}")
    private Integer readerMaxPoolSize;

    private HikariDataSource getHikariDataSource(String username, String password) {
        HikariDataSource hds = DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .username(username)
            .password(password)
            .build();

        hds.setConnectionTimeout(this.connectionTimeout);
        hds.setValidationTimeout(this.validationTimeout);
        hds.setLeakDetectionThreshold(this.leakDetectionThreshold);
        hds.setDataSourceProperties(dataSourceProperties);

        return hds;
    }

    public HikariDataSource getWriterDataSource() {
        HikariDataSource hds = this.getHikariDataSource(this.writerUsername, this.writerPassword);
        hds.setJdbcUrl(this.writerJdbcUrl);
        hds.setMinimumIdle(this.writerMinIdle);
        hds.setMaximumPoolSize(this.writerMaxPoolSize);
        return hds;
    }

    public HikariDataSource getReaderDataSource() {
        if (StringUtils.isBlank(this.readerJdbcUrl)) {
            this.readerJdbcUrl = this.writerJdbcUrl;
            this.readerUsername = this.writerUsername;
            this.readerPassword = this.writerPassword;
        }

        HikariDataSource hds = this.getHikariDataSource(this.readerUsername, this.readerPassword);
        hds.setJdbcUrl(this.readerJdbcUrl);
        hds.setMinimumIdle(this.readerMinIdle);
        hds.setMaximumPoolSize(this.readerMaxPoolSize);
        return hds;
    }

}