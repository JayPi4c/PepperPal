package com.jaypi4c.chilirestapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("sqlite.driver-class-name")));
        dataSource.setUrl(env.getProperty("sqlite.url"));
        return dataSource;
    }
}
