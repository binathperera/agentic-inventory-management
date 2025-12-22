package com.inventory.management.model;

//Enum for user roles with different access levels and permissions.

public enum UserRole {
    
    //ADMIN - Full system access. Can manage all products, suppliers, users, and system settings.
    
    ADMIN("System Administrator", 1),
    
    //PRODUCT_MANAGER - Can manage all products and view supplier data.
    
    PRODUCT_MANAGER("Product Manager", 2),
    
    /**
     * SUPPLIER_HANDLER - Can only access and manage supplier data.
     * Can view products but cannot edit or delete them.
     */
    SUPPLIER_HANDLER("Supplier Handler", 3),
    
    /**
     * INVENTORY_MANAGER - Can manage stock levels and inventory data.
     * Can view products and suppliers but can only modify stock quantities.
     */
    INVENTORY_MANAGER("Inventory Manager", 4),
    
    /**
     * USER - Standard user with basic access. Can view products and create new ones.
     * Limited to read-only access for supplier data and cannot delete any records.
     */
    USER("Standard User", 5);
    
    private final String description;
    private final int level;
    
    UserRole(String description, int level) {
        this.description = description;
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Check if this role has a higher or equal privilege level than another role.
     * Lower level numbers have higher privileges.
     */
    public boolean hasHigherOrEqualPrivilege(UserRole other) {
        return this.level <= other.level;
    }
}
