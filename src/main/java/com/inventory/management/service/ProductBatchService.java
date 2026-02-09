package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.ProductBatch;
import com.inventory.management.repository.ProductBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductBatchService {

    @Autowired
    private ProductBatchRepository productBatchRepository;

    private String tenant() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }

    public List<ProductBatch> getAll() {
        return productBatchRepository.findByTenantId(tenant());
    }

    public List<ProductBatch> getByInvoice(String invoiceNo) {
        return productBatchRepository.findByTenantIdAndInvoiceNo(tenant(), invoiceNo);
    }

    public List<ProductBatch> getByProduct(String productId) {
        return productBatchRepository.findByTenantIdAndProductId(tenant(), productId);
    }

    public List<ProductBatch> getExpiringBefore(LocalDate date) {
        return productBatchRepository.findByTenantIdAndExpBefore(tenant(), date);
    }

    public ProductBatch create(ProductBatch batch) {
        batch.setTenantId(tenant());
        return productBatchRepository.save(batch);
    }
}
