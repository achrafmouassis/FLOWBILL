package com.flowbill.common.multitenancy;

import org.springframework.jdbc.datasource.DelegatingDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TenantRoutingDataSource extends DelegatingDataSource {

    public TenantRoutingDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        String tenant = TenantContext.getCurrentTenant();
        try (Statement stmt = connection.createStatement()) {
            if (tenant != null) {
                // Sanitize tenant ID to prevent SQL injection (simple check)
                if (!tenant.matches("^[a-zA-Z0-9_]+$")) {
                    throw new SQLException("Invalid tenant ID: " + tenant);
                }
                stmt.execute("SET search_path TO " + tenant + ", public");
            } else {
                stmt.execute("SET search_path TO public");
            }
        }
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        String tenant = TenantContext.getCurrentTenant();
        try (Statement stmt = connection.createStatement()) {
            if (tenant != null) {
                if (!tenant.matches("^[a-zA-Z0-9_]+$")) {
                    throw new SQLException("Invalid tenant ID: " + tenant);
                }
                stmt.execute("SET search_path TO " + tenant + ", public");
            } else {
                stmt.execute("SET search_path TO public");
            }
        }
        return connection;
    }
}
