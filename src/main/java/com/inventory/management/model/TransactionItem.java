package com.inventory.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transaction_item")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_transaction_product_idx", def = "{'tenant_id': 1, 'transaction_id': 1, 'product_id': 1}", unique = true),
        @CompoundIndex(name = "tenant_transaction_idx", def = "{'tenant_id': 1, 'transaction_id': 1}"),
        @CompoundIndex(name = "tenant_product_idx", def = "{'tenant_id': 1, 'product_id': 1}")
})
public class TransactionItem {

    @Id
    private String id;

    @Field("tenant_id")
    @Indexed
    private String tenantId;

    @Field("transaction_id")
    @Indexed
    private String transactionId;

    @Field("product_id")
    @Indexed
    private String productId;

    @Field("qty")
    private Integer qty;

    @Field("unit_price")
    private Double unitPrice;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("schema_version")
    private int schemaVersion = 1;

    public TransactionItem(String tenantId, String transactionId, String productId,
            Integer qty, Double unitPrice) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("transactionId cannot be null or empty");
        }
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (qty == null || qty <= 0) {
            throw new IllegalArgumentException("qty must be greater than zero");
        }
        if (unitPrice == null || unitPrice < 0) {
            throw new IllegalArgumentException("unitPrice cannot be null or negative");
        }

        this.tenantId = tenantId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    /**
     * Calculate line total (qty * unit_price)
     */
    public Double getLineTotal() {
        return qty != null && unitPrice != null ? qty * unitPrice : 0.0;
    }
}
