package com.inventory.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audit_log")
@CompoundIndex(name = "tenant_user_timestamp_idx", def = "{'tenant_id': 1, 'user_id': 1, 'timestamp': -1}")
@CompoundIndex(name = "entity_timestamp_idx", def = "{'entity_type': 1, 'entity_id': 1, 'timestamp': -1}")
public class AuditLog {

    @Id
    private String logId;

    @Field("tenant_id")
    @Indexed
    private String tenantId;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("username")
    private String username;

    @Field("entity_type")
    @Indexed
    private String entityType; // "Product", "User", "Supplier", "Transaction", etc.

    @Field("entity_id")
    @Indexed
    private String entityId; // The ID of the affected entity

    @Field("action_type")
    @Indexed
    private String actionType; // "CREATE", "READ", "UPDATE", "DELETE", "EXPORT", "IMPORT", etc.

    @Field("description")
    private String description; // Human-readable description of the action

    @Field("old_values")
    private Map<String, Object> oldValues; // Previous values for UPDATE operations

    @Field("new_values")
    private Map<String, Object> newValues; // New values for CREATE/UPDATE operations

    @Field("status")
    private String status; // "SUCCESS", "FAILURE"

    @Field("error_message")
    private String errorMessage; // Error details if status is FAILURE

    @Field("ip_address")
    private String ipAddress;

    @Field("user_agent")
    private String userAgent;

    @Field("request_path")
    private String requestPath; // e.g., "/api/products/123"

    @Field("request_method")
    private String requestMethod; // GET, POST, PUT, DELETE, PATCH

    @Field("duration_ms")
    private Long durationMs; // How long the operation took

    @CreatedDate
    @Field("timestamp")
    @Indexed
    private Instant timestamp;

    @Field("schema_version")
    private int schemaVersion = 1;

    public enum ActionType {
        CREATE("CREATE"),
        READ("READ"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        EXPORT("EXPORT"),
        IMPORT("IMPORT"),
        DOWNLOAD("DOWNLOAD"),
        UPLOAD("UPLOAD"),
        LOGIN("LOGIN"),
        LOGOUT("LOGOUT"),
        PERMISSION_CHANGE("PERMISSION_CHANGE"),
        CONFIG_CHANGE("CONFIG_CHANGE");

        public final String value;

        ActionType(String value) {
            this.value = value;
        }
    }

    public enum Status {
        SUCCESS("SUCCESS"),
        FAILURE("FAILURE");

        public final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
