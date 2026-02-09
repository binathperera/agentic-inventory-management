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
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoice")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_invoice_idx", def = "{'tenant_id': 1, 'invoice_no': 1}", unique = true),
        @CompoundIndex(name = "tenant_supplier_idx", def = "{'tenant_id': 1, 'supplier_id': 1}"),
        @CompoundIndex(name = "tenant_date_idx", def = "{'tenant_id': 1, 'date': -1}")
})
public class Invoice {

    @Id
    private String id;

    @Field("tenant_id")
    @Indexed
    private String tenantId;

    @Field("invoice_no")
    @Indexed
    private String invoiceNo;

    @Field("supplier_id")
    @Indexed
    private String supplierId;

    @Field("date")
    private LocalDate date;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("schema_version")
    private int schemaVersion = 1;

    public Invoice(String tenantId, String invoiceNo, String supplierId, LocalDate date) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (invoiceNo == null || invoiceNo.isEmpty()) {
            throw new IllegalArgumentException("invoiceNo cannot be null or empty");
        }
        if (supplierId == null || supplierId.isEmpty()) {
            throw new IllegalArgumentException("supplierId cannot be null or empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }

        this.tenantId = tenantId;
        this.invoiceNo = invoiceNo;
        this.supplierId = supplierId;
        this.date = date;
    }
}
