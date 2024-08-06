package com.ayeshascode.customer.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresRunner implements BeforeAllCallback {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("test")
                    .withUsername("user")
                    .withPassword("password");

    @Override
    public void beforeAll(ExtensionContext context) {
        postgresContainer.start();
        System.setProperty("POSTGRESQL_DB_URL", postgresContainer.getJdbcUrl());
        System.setProperty("POSTGRESQL_DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("POSTGRESQL_DB_PASSWORD", postgresContainer.getPassword());
    }

    public static PostgreSQLContainer<?> getPostgresContainer() {
        return postgresContainer;
    }
}
