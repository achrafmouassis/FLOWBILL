package com.flowbill.project.config;

public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRoles = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void setCurrentRoles(String roles) {
        currentRoles.set(roles);
    }

    public static String getCurrentRoles() {
        return currentRoles.get();
    }

    public static void clear() {
        currentTenant.remove();
        currentUserId.remove();
        currentRoles.remove();
    }
}
