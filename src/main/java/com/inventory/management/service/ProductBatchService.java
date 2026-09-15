package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Product;
import com.inventory.management.model.ProductBatch;
import com.inventory.management.repository.ProductBatchRepository;
import com.inventory.management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ProductBatchService {

    @Autowired
    private ProductBatchRepository productBatchRepository;

    @Autowired
    private ProductRepository productRepository;

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
        String tenantId = tenant();
        batch.setTenantId(tenantId);
        ProductBatch saved = productBatchRepository.save(batch);
        adjustProductQuantity(saved.getProductId(), saved.getQty(), tenantId);
        return saved;
    }

    public ProductBatch update(String id, ProductBatch changes) {
        String tenantId = tenant();
        ProductBatch batch = getById(id);
        String previousProductId = batch.getProductId();
        int previousQty = quantity(batch.getQty());
        batch.setProductId(changes.getProductId());
        batch.setInvoiceNo(changes.getInvoiceNo());
        batch.setBatchNo(changes.getBatchNo());
        batch.setQty(changes.getQty());
        batch.setUnitCost(changes.getUnitCost());
        batch.setUnitPrice(changes.getUnitPrice());
        batch.setExp(changes.getExp());
        ProductBatch saved = productBatchRepository.save(batch);
        if (Objects.equals(previousProductId, saved.getProductId())) {
            adjustProductQuantity(saved.getProductId(), quantity(saved.getQty()) - previousQty, tenantId);
        } else {
            adjustProductQuantity(previousProductId, -previousQty, tenantId);
            adjustProductQuantity(saved.getProductId(), quantity(saved.getQty()), tenantId);
        }
        return saved;
    }

    public void delete(String id) {
        ProductBatch batch = getById(id);
        productBatchRepository.delete(batch);
        adjustProductQuantity(batch.getProductId(), -quantity(batch.getQty()), tenant());
    }

    private int quantity(Integer value) {
        return value == null ? 0 : value;
    }

    private void adjustProductQuantity(String productId, int quantityDelta, String tenantId) {
        Product product = productRepository.findById(productId)
                .filter(candidate -> tenantId.equals(candidate.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        int currentQuantity = product.getRemainingQuantity() == null ? 0 : product.getRemainingQuantity();
        int updatedQuantity = currentQuantity + quantityDelta;
        if (updatedQuantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        product.setRemainingQuantity(updatedQuantity);
        productRepository.save(product);
    }

    private ProductBatch getById(String id) {
        return productBatchRepository.findById(id)
                .filter(batch -> tenant().equals(batch.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product batch not found: " + id));
    }
}
