package com.flowbill.billing.config;

import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_ROLES = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentRoles(String roles) {
        CURRENT_ROLES.set(roles);
    }

    public static String getCurrentRoles() {
        return CURRENT_ROLES.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_ROLES.remove();
    }
}
