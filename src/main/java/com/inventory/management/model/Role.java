package com.inventory.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "role")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private String id; // e.g., "ROLE_ADMIN"
    private String description;
    private int level;
    // The "Matrix": Map of Resource -> Action
    // Example: "products" -> ["READ", "WRITE", "DELETE"]
    private Map<String, List<String>> permissions; 
}