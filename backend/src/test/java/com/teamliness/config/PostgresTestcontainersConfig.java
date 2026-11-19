package com.teamliness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configures PostgreSQL Testcontainers when a local/CI Docker daemon is reachable,
 * or falls back cleanly to PostgreSQL-compatibility mode so single-run ATDD suites
 * remain executable on hosts without Docker Desktop running.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestcontainersConfig {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresTestcontainersConfig.class);
    private static PostgreSQLContainer<?> sharedPostgres;

    static {
        try {
            if (DockerClientFactory.instance().isDockerAvailable()) {
                sharedPostgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                        .withDatabaseName("teamliness")
                        .withUsername("teamliness")
                        .withPassword("teamliness_test_password");
                sharedPostgres.start();
                LOG.info("Started Testcontainers PostgreSQL 16 at {}", sharedPostgres.getJdbcUrl());
            } else {
                LOG.info("Docker daemon not available; using PostgreSQL-mode test datasource fallback.");
            }
        } catch (Throwable ex) {
            LOG.info("Docker daemon probe did not succeed ({}); using PostgreSQL-mode test datasource fallback.", ex.getMessage());
            sharedPostgres = null;
        }
    }

    /**
     * Registers dynamic datasource properties pointing to Testcontainers PostgreSQL if running.
     *
     * @return dynamic property registrar
     */
    @Bean
    public DynamicPropertyRegistrar postgresPropertyRegistrar() {
        return registry -> {
            if (sharedPostgres != null && sharedPostgres.isRunning()) {
                registry.add("spring.datasource.url", sharedPostgres::getJdbcUrl);
                registry.add("spring.datasource.username", sharedPostgres::getUsername);
                registry.add("spring.datasource.password", sharedPostgres::getPassword);
                registry.add("spring.datasource.driver-class-name", sharedPostgres::getDriverClassName);
            }
        };
    }
}
