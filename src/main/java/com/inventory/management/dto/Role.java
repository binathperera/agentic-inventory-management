package com.inventory.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Role {
    private String id; // e.g., "ROLE_ADMIN"
    private String description;
    private int level;
    // The "Matrix": Map of Resource -> Action
    // Example: "products" -> ["READ", "WRITE", "DELETE"]
    private Map<String, List<String>> permissions; 
}