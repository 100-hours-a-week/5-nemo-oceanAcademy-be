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

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(createDataSource("prod"));

        return routingDataSource;
    }

    private DataSource createDataSource(String profile) {
        DatabaseProperties.DbConfig dbConfig = profile.equals("dev") ? databaseProperties.getDev() : databaseProperties.getProd();

        return DataSourceBuilder.create()
                .url(dbConfig.getUrl())
                .username(dbConfig.getUsername())
                .password(dbConfig.getPassword())
                .build();
    }
}