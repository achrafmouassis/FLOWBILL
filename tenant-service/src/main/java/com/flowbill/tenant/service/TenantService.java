package com.flowbill.tenant.service;

import com.flowbill.tenant.entity.Tenant;
import com.flowbill.tenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantMigrationService tenantMigrationService;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenant(String id) {
        return tenantRepository.findById(id).orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    public Tenant createTenant(Tenant tenant) {
        // 1. Save Tenant Metadata
        // Ensure schemaName is set based on ID if not provided
        if (tenant.getSchemaName() == null) {
            tenant.setSchemaName("tenant_" + tenant.getId());
        }

        Tenant savedTenant = tenantRepository.save(tenant);

        // 2. Create Schema & Run Migrations
        tenantMigrationService.createTenantSchema(tenant.getSchemaName());
        tenantMigrationService.runMigrations(tenant.getSchemaName());

        return savedTenant;
    }
}
