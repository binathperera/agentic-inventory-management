package com.inventory.management.repository;

import com.inventory.management.model.ProductBatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends MongoRepository<ProductBatch, String> {

    /**
     * Find all batches for a specific tenant
     */
    List<ProductBatch> findByTenantId(String tenantId);

    /**
     * Find all batches for a specific product within a tenant
     */
    List<ProductBatch> findByTenantIdAndProductId(String tenantId, String productId);

    /**
     * Find all batches for a specific invoice within a tenant
     */
    List<ProductBatch> findByTenantIdAndInvoiceNo(String tenantId, String invoiceNo);

    /**
     * Find a specific batch by tenant, product, and invoice
     */
    Optional<ProductBatch> findByTenantIdAndProductIdAndInvoiceNo(String tenantId, String productId, String invoiceNo);

    /**
     * Find batches by batch number within a tenant
     */
    List<ProductBatch> findByTenantIdAndBatchNo(String tenantId, String batchNo);

    /**
     * Find batches by product and batch number
     */
    Optional<ProductBatch> findByTenantIdAndProductIdAndBatchNo(String tenantId, String productId, String batchNo);

    /**
     * Find batches expiring before a certain date
     */
    List<ProductBatch> findByTenantIdAndExpBefore(String tenantId, LocalDate date);

    /**
     * Find batches expiring within a date range
     */
    List<ProductBatch> findByTenantIdAndExpBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    /**
     * Find batches for a product with remaining quantity > 0
     */
    List<ProductBatch> findByTenantIdAndProductIdAndQtyGreaterThan(String tenantId, String productId, Integer qty);

    /**
     * Check if a batch exists for tenant, product, and invoice
     */
    Boolean existsByTenantIdAndProductIdAndInvoiceNo(String tenantId, String productId, String invoiceNo);
}
