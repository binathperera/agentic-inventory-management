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
@Document(collection = "product_batch")
@CompoundIndexes({
    @CompoundIndex(name = "tenant_product_invoice_idx", def = "{'tenant_id': 1, 'product_id': 1, 'invoice_no': 1}", unique = true),
    @CompoundIndex(name = "tenant_product_batch_idx", def = "{'tenant_id': 1, 'product_id': 1, 'batch_no': 1}"),
    @CompoundIndex(name = "tenant_invoice_idx", def = "{'tenant_id': 1, 'invoice_no': 1}")
})
public class ProductBatch {
    
    @Id
    private String id;
    
    @Field("tenant_id")
    @Indexed
    private String tenantId;
    
    @Field("product_id")
    @Indexed
    private String productId;
    
    @Field("invoice_no")
    @Indexed
    private String invoiceNo;
    
    @Field("batch_no")
    private String batchNo;
    
    @Field("qty")
    private Integer qty;
    
    @Field("unit_cost")
    private Float unitCost;
    
    @Field("unit_price")
    private Float unitPrice;
    
    @Field("exp")
    private LocalDate exp; // expiration date
    
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    
    @Field("schema_version")
    private int schemaVersion = 1;
    
    public ProductBatch(String tenantId, String productId, String invoiceNo, String batchNo, 
                       Integer qty, Float unitCost, Float unitPrice, LocalDate exp) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new IllegalArgumentException("tenantId cannot be null or empty");
        }
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (invoiceNo == null || invoiceNo.isEmpty()) {
            throw new IllegalArgumentException("invoiceNo cannot be null or empty");
        }
        if (batchNo == null || batchNo.isEmpty()) {
            throw new IllegalArgumentException("batchNo cannot be null or empty");
        }
        if (qty == null || qty < 0) {
            throw new IllegalArgumentException("qty cannot be null or negative");
        }
        if (unitCost == null || unitCost < 0) {
            throw new IllegalArgumentException("unitCost cannot be null or negative");
        }
        if (unitPrice == null || unitPrice < 0) {
            throw new IllegalArgumentException("unitPrice cannot be null or negative");
        }
        
        this.tenantId = tenantId;
        this.productId = productId;
        this.invoiceNo = invoiceNo;
        this.batchNo = batchNo;
        this.qty = qty;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.exp = exp;
    }
}
