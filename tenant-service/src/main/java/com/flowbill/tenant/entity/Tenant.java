package com.flowbill.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Data
public class Tenant {
    @Id
    private String id; // e.g. "acme", "alpha"

    private String name;

    @Column(name = "schema_name")
    private String schemaName; // e.g. "tenant_acme"

    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
