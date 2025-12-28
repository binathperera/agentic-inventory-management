package com.inventory.management.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Document(collection = "tenant")
@Data
public class Tenant {
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("sub_domain")
    private String subDomain;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    @Field("schema_version")
    private int schemaVersion = 1;

    public Tenant(String name, String subDomain) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if(subDomain == null || subDomain.isEmpty()) {
            throw new IllegalArgumentException("subDomain cannot be null or empty");
        }
        this.name = name;
        this.subDomain = subDomain;
    }
}
