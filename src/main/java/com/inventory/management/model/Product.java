package com.inventory.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
    
    @Indexed(unique = true)
    private String sku;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private String category;
    
    private String supplier;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
