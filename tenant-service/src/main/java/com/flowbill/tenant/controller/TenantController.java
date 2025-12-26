package com.flowbill.tenant.controller;

import com.flowbill.tenant.entity.Tenant;
import com.flowbill.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @PostMapping
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }

    @GetMapping("/{id}")
    public Tenant getTenant(@PathVariable String id) {
        return tenantService.getTenant(id);
    }
}
