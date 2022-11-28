package com.lguplus.fleta.config.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private static final String ROUTING_DS = "routingDataSource";
    private static final String LAZY_DS = "lazyConnectionDataSource";

    private final TargetDataSourceFactory targetDataSourceFactory;

    @Bean(ROUTING_DS)
    public DataSource routingDataSource() throws SQLException {
        DataSource writerDataSource = createWriterDataSource();
        DataSource readerDataSource = createReaderDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(Target.WRITER, writerDataSource);
        targetDataSources.put(Target.READER, readerDataSource);

        return RoutingDataSource.create(targetDataSources, writerDataSource);
    }

    @Primary
    @Bean(LAZY_DS)
    @DependsOn(ROUTING_DS)
    public DataSource lazyConnectionDataSource(@Qualifier(ROUTING_DS) DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    @Bean
    @DependsOn(LAZY_DS)
    public PlatformTransactionManager transactionManager(@Qualifier(LAZY_DS) DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    private DataSource createWriterDataSource() {
        return targetDataSourceFactory.createWriter();
    }

    private DataSource createReaderDataSource() throws SQLException {
        DataSource readerDataSource = targetDataSourceFactory.createReader();
        /*
         * 애플리케이션 구동 초기에 Reader 커넥션 풀도 함께 생성 처리
         *
         * AbstractRoutingDataSource에 의해 애플리케이션 구동 시 Writer 커넥션 풀만 생성되고
         * 구동 이후, 아래 중 하나의 시점에 Reader 커넥션 풀이 지연 생성되기 때문에,
         * Writer 커넥션 풀 생성과 동일한 시점에 Reader 커넥션 풀을 생성하기 위함
         *  1) @Transactional(readOnly = true)이 선언된 메소드가 최초 호출될 때
         *  2) 또는, 애플리케이션 구동 최종 완료 이후 (ApplicationReadyEvent 발생 이후)
         */
        createExplicitlyConnectionPoolOf(readerDataSource);
        return readerDataSource;
    }

    /**
     * (커넥션 획득 메서드 호출을 통한) 명시적인 커넥션 풀 생성
     */
    private void createExplicitlyConnectionPoolOf(DataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        if (Objects.nonNull(conn)) {
            conn.close();
        }
    }

    private enum Target {
        WRITER, READER
    }

    @Slf4j
    private static class RoutingDataSource extends AbstractRoutingDataSource {

        private static DataSource create(Map<Object, Object> targetDataSources, Object defaultTargetDataSource) {
            RoutingDataSource dataSource = new RoutingDataSource();
            dataSource.setTargetDataSources(targetDataSources);
            dataSource.setDefaultTargetDataSource(defaultTargetDataSource);
            return dataSource;
        }

        @Override
        protected Object determineCurrentLookupKey() {
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            Target target = readOnly ? Target.READER : Target.WRITER;
            log.debug("routing: {}", target);
            return target;
        }
    }
}
