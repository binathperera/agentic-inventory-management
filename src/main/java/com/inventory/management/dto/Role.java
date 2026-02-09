package com.inventory.management.dto;

import lombok.Data;
import com.inventory.management.enums.RoleEnum;

@Data
public class Role implements Comparable<Role> {
    private String name;  // "ADMIN", "CASHIER", "USER"

    public Role() {
    }

    public Role(String name) {
        this.name = name.toUpperCase();
        // Validate that it's a valid role
        RoleEnum.fromString(this.name);
    }

    public Role(RoleEnum roleEnum) {
        this.name = roleEnum.name();
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role other = (Role) obj;
        return name != null && name.equals(other.name);
    }

    @Override
    public int compareTo(Role other) {
        if (other == null) return 1;
        return this.name.compareTo(other.name);
    }
}
