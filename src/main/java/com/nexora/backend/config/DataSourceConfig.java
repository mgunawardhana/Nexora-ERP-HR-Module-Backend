package com.nexora.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.write")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource writeDataSource(@Qualifier("writeDataSourceProperties") DataSourceProperties dataSourceProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());
        config.setDriverClassName(dataSourceProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    /**
     * Returns the JdbcTemplate for write operations.
     *
     * @param dataSource @{@link DataSource}
     * @return @{@link JdbcTemplate}
     */
    @Bean(name = "writeJdbcTemplate")
    public JdbcTemplate writeJdbcTemplate(@Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource(@Qualifier("readDataSourceProperties") DataSourceProperties dataSourceProperties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());
        config.setDriverClassName(dataSourceProperties.getDriverClassName());
        return new HikariDataSource(config);
    }

    /**
     * Returns the JdbcTemplate for read operations.
     *
     * @param dataSource @{@link DataSource}
     * @return @{@link JdbcTemplate}
     */
    @Bean(name = "readJdbcTemplate")
    public JdbcTemplate readJdbcTemplate(@Qualifier("readDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
