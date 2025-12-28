package com.inventory.management.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "supplier")
@CompoundIndex(name = "tenant_supplier_idx", def = "{'tenant_id': 1, '_id': 1}", unique = true)
public class Supplier {
    @Field("tenant_id")
    private String tenantId;
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("email")
    private String email;
    @Field("address")
    private String address;
    @Field("contact")
    private String contact;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    @Field("schema_version")
    private int schemaVersion = 1;

    public Supplier(String tenantId, String name, String email, String contact, String address) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        if (contact == null || contact.isEmpty()) {
            throw new IllegalArgumentException("contact cannot be null or empty");
        }
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address cannot be null or empty");
        }
        this.tenantId = tenantId;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.address = address;
    }
}
