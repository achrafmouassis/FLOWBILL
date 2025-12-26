package com.flowbill.billing.config;

import com.flowbill.common.multitenancy.TenantRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties globalDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource globalDataSource() {
        return globalDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("globalDataSource") DataSource globalDataSource) {
        return new TenantRoutingDataSource(globalDataSource);
    }
}
