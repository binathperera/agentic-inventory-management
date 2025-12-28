package com.inventory.management.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "tenant")
@Data
public class Tenant {
    @Id
    private String id;
    private String name;
    @CreatedDate
    private Instant created_at;
    @LastModifiedDate
    private Instant updated_at;
    private int schema_version = 1;

    public Tenant(String name) {
        this.name = name;
    }
}
