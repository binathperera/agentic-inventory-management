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
@Document(collection = "transaction")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_transaction_idx", def = "{'tenant_id': 1, 'transaction_id': 1}", unique = true),
        @CompoundIndex(name = "tenant_date_idx", def = "{'tenant_id': 1, 'created_at': -1}")
})
public class Transaction {

    @Id
    private String id;

    @Field("tenant_id")
    @Indexed
    private String tenantId;

    @Field("transaction_id")
    @Indexed
    private String transactionId;

    @Field("payment_method")
    private String paymentMethod;

    @Field("gross_amount")
    private Double grossAmount;

    @Field("discount_amount")
    private Double discountAmount;

    @Field("net_amount")
    private Double netAmount;

    @Field("paid_amount")
    private Double paidAmount;

    @Field("balance_amount")
    private Double balanceAmount;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("schema_version")
    private int schemaVersion = 1;

    public Transaction(String tenantId, String transactionId, String paymentMethod,
            Double grossAmount, Double discountAmount, Double netAmount,
            Double paidAmount, Double balanceAmount) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("transactionId cannot be null or empty");
        }
        if (grossAmount == null || grossAmount < 0) {
            throw new IllegalArgumentException("grossAmount cannot be null or negative");
        }
        if (netAmount == null || netAmount < 0) {
            throw new IllegalArgumentException("netAmount cannot be null or negative");
        }
        if (paidAmount == null || paidAmount < 0) {
            throw new IllegalArgumentException("paidAmount cannot be null or negative");
        }

        this.tenantId = tenantId;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.grossAmount = grossAmount;
        this.discountAmount = discountAmount != null ? discountAmount : 0.0;
        this.netAmount = netAmount;
        this.paidAmount = paidAmount;
        this.balanceAmount = balanceAmount != null ? balanceAmount : 0.0;
    }
}