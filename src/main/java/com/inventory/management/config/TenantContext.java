package com.inventory.management.config;

public class TenantContext {
    // ThreadLocal to hold tenant identifier for the current thread. Used during initial login before storing in JWT.
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
