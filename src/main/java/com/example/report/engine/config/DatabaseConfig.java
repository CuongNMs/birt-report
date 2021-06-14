package com.example.report.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Value("${oracle.driver.name}")
    private String driverName;
    @Value("${oracle.driver.url}")
    private String driverUrl;
    @Value("${oracle.driver.username}")
    private String driverUsername;
    @Value("${oracle.driver.password}")
    private String driverPassword;

    @Bean(name="oracleSource")
    public DataSource getDataSource(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverName);
        dataSourceBuilder.url(driverUrl);
        dataSourceBuilder.username(driverUsername);
        dataSourceBuilder.password(driverPassword);
        return dataSourceBuilder.build();
    }
}
