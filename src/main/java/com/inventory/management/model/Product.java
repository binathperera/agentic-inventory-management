package com.inventory.management.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Data
@Document(collection = "product")
@CompoundIndex(name = "tenant_product_idx", def = "{'tenant_id': 1, '_id': 1}", unique = true)
public class Product {
    @Field("tenant_id")
    private String tenantId;
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("latest_batch_no")
    private String latestBatchNo;
    @Field("remaining_quantity")
    private Integer remainingQuantity;
    @Field("latest_unit_price")
    private float latestUnitPrice;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    @Field("schema_version")
    private int schemaVersion = 1;

    public Product(String tenantId, String id, String name, String latestBatchNo, Integer remainingQuantity,
            float latestUnitPrice) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (latestBatchNo == null || latestBatchNo.isEmpty()) {
            throw new IllegalArgumentException("latestBatchNo cannot be null or empty");
        }
        if (remainingQuantity == null || remainingQuantity < 0) {
            throw new IllegalArgumentException("remainingQuantity cannot be null or negative");
        }
        if (latestUnitPrice < 0) {
            throw new IllegalArgumentException("latestUnitPrice cannot be negative");
        }
        this.tenantId = tenantId;
        this.id = id;
        this.name = name;
        this.latestBatchNo = latestBatchNo;
        this.remainingQuantity = remainingQuantity;
        this.latestUnitPrice = latestUnitPrice;
    }
}
