package com.inventory.management.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Document(collection = "supplier")
public class Supplier {
    private String tenant_id;
    @Id
    private String _id;
    private String name;
    private String email;
    private String address;
    private String contact;
    @CreatedDate
    private Instant created_at;
    @LastModifiedDate
    private Instant updated_at;
    private int schema_version = 1;

    public Supplier(String tenant_id, String name, String email, String contact, String address) {
        this.tenant_id = tenant_id;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.address = address;
    }
}
