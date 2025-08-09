package com.jmumo.mortgage.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    // Connection pool constants
    private static final int PROD_MAX_POOL_SIZE = 20;
    private static final int PROD_MIN_IDLE = 5;
    private static final int PROD_IDLE_TIMEOUT = 300000;
    private static final int PROD_CONNECTION_TIMEOUT = 20000;
    private static final int PROD_MAX_LIFETIME = 1200000;
    private static final int PROD_LEAK_DETECTION_THRESHOLD = 60000;
    private static final int PROD_VALIDATION_TIMEOUT = 5000;

    private static final int DEV_MAX_POOL_SIZE = 5;
    private static final int DEV_MIN_IDLE = 2;
    private static final int DEV_IDLE_TIMEOUT = 600000;
    private static final int DEV_CONNECTION_TIMEOUT = 30000;
    private static final int DEV_MAX_LIFETIME = 1800000;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Production optimizations
        config.setMaximumPoolSize(PROD_MAX_POOL_SIZE);
        config.setMinimumIdle(PROD_MIN_IDLE);
        config.setIdleTimeout(PROD_IDLE_TIMEOUT);
        config.setConnectionTimeout(PROD_CONNECTION_TIMEOUT);
        config.setMaxLifetime(PROD_MAX_LIFETIME);
        config.setLeakDetectionThreshold(PROD_LEAK_DETECTION_THRESHOLD);

        // Connection validation
        config.setValidationTimeout(PROD_VALIDATION_TIMEOUT);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Development settings - smaller pool for local dev
        config.setMaximumPoolSize(DEV_MAX_POOL_SIZE);
        config.setMinimumIdle(DEV_MIN_IDLE);
        config.setIdleTimeout(DEV_IDLE_TIMEOUT);
        config.setConnectionTimeout(DEV_CONNECTION_TIMEOUT);
        config.setMaxLifetime(DEV_MAX_LIFETIME);

        return new HikariDataSource(config);
    }
}