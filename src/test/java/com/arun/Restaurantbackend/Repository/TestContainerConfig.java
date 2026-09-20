package com.arun.Restaurantbackend.Repository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {

        return new PostgreSQLContainer<>("postgres:16")
                .withEnv("TZ", "UTC")
                .withEnv("PGTZ", "UTC").withCommand("postgres", "-c", "timezone=UTC");
    }




}