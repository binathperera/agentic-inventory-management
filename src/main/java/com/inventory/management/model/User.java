package com.inventory.management.model;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.inventory.management.dto.Role;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "user")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_username_idx", def = "{'tenant_id': 1, 'username': 1}", unique = true),
        @CompoundIndex(name = "tenant_email_idx", def = "{'tenant_id': 1, 'email': 1}", unique = true)
})
public class User {
    @Field("tenant_id")
    private String tenantId;
    @Id
    private String id;
    @Field("username")
    private String username;
    @Field("email")
    private String email;
    @Field("password")
    private String password;
    @Field("roles")
    private Set<Role> roles = new HashSet<>();
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    @Field("schema_version")
    private int schemaVersion = 1;
    @Field("enabled")
    private boolean enabled = true;
    public User(String tenantId, String username, String email, String password) {
        if(tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        if(password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
        this.tenantId = tenantId;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
