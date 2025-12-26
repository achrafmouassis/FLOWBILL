package com.flowbill.tenant.config;

import com.flowbill.tenant.entity.Tenant;
import com.flowbill.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantDataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking for initial tenant seeding...");

        if (tenantRepository.findById("acme").isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setId("acme");
            tenant.setName("Acme Corporation");
            tenant.setSchemaName("tenant_acme");
            tenant.setActive(true);

            tenantRepository.save(tenant);
            log.info("Seeded default tenant: acme");
        } else {
            log.info("Default tenant 'acme' already exists.");
        }
    }
}
