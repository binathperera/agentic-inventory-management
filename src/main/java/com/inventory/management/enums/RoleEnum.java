package com.inventory.management.enums;

/**
 * Role enumeration for user access control in the inventory management system.
 * 
 * ADMIN:   Full system access including user management, configuration, and deletions
 * MANAGER: Manage Inventory, Suppliers, Invoices, Sales, Product Batches
 * CASHIER: Transaction and sales access, can view inventory
 * USER:    Read-only access to inventory and reports, cannot modify data
 */
public enum RoleEnum {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    CASHIER("CASHIER"),
    USER("USER");

    private final String displayName;

    RoleEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RoleEnum fromString(String name) {
        try {
            return RoleEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role name: " + name + ". Valid roles are: ADMIN, MANAGER, CASHIER, USER");
        }
    }
}
