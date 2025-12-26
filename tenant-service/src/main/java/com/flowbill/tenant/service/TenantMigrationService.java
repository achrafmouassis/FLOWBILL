package com.flowbill.tenant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantMigrationService {

    private final DataSource dataSource;

    public void createTenantSchema(String schemaName) {
        log.info("Creating schema: {}", schemaName);
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        } catch (Exception e) {
            log.error("Error creating schema: {}", schemaName, e);
            throw new RuntimeException("Failed to create schema " + schemaName, e);
        }
    }

    public void runMigrations(String schemaName) {
        log.info("Running migrations for schema: {}", schemaName);
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenants")
                    .load();

            flyway.migrate();
            log.info("Migrations completed successfully for schema: {}", schemaName);
        } catch (Exception e) {
            log.error("Error running migrations for schema: {}", schemaName, e);
            throw new RuntimeException("Failed to run migrations for " + schemaName, e);
        }
    }
}
