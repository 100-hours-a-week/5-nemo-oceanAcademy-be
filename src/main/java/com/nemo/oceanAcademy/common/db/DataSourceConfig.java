package com.nemo.oceanAcademy.common.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Bean
    public DataSource dataSource() {
        SubdomainRoutingDataSource routingDataSource = new SubdomainRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("dev", createDataSource("dev"));
        dataSourceMap.put("prod", createDataSource("prod"));

        // 기본적으로 prod 데이터베이스를 사용
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(createDataSource("prod"));  // 기본 데이터베이스 설정

        return routingDataSource;
    }

    private DataSource createDataSource(String profile) {
        DatabaseProperties.DbConfig dbConfig = "dev".equals(profile) ? databaseProperties.getDev() : databaseProperties.getProd();

        if (dbConfig == null) {
            throw new IllegalStateException("Database configuration for profile " + profile + " is missing");
        }

        return DataSourceBuilder.create()
                .url(dbConfig.getUrl())
                .username(dbConfig.getUsername())
                .password(dbConfig.getPassword())
                .build();
    }
}
