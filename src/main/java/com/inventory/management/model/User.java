package com.inventory.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
@CompoundIndexes({
    @CompoundIndex(name = "tenant_username_idx", def = "{'tenant_id': 1, 'username': 1}", unique = true),
    @CompoundIndex(name = "tenant_email_idx", def = "{'tenant_id': 1, 'email': 1}", unique = true)
})
public class User {
    private String tenant_id;
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean enabled = true;
    private int schema_version = 1;
}
