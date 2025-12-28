package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.ProductDTO;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Product;
import com.inventory.management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        String tenantId = requireTenantId();
        return productRepository.findAll().stream()
                .filter(product -> tenantId.equals(product.getTenantId()))
                .toList();
    }

    public Product getProductById(String id) {
        String tenantId = requireTenantId();
        return productRepository.findById(id)
                .filter(product -> tenantId.equals(product.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(ProductDTO productRequest) {
        String tenantId = requireTenantId();
        Product product = new Product(
                tenantId,
                productRequest.getId(),
                productRequest.getName(),
                productRequest.getLatestBatchNo(),
                productRequest.getQuantity(),
                productRequest.getLatestUnitPrice());
        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductDTO productRequest) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(productRequest.getName());
        existingProduct.setLatestBatchNo(productRequest.getLatestBatchNo());
        existingProduct.setRemainingQuantity(productRequest.getQuantity());
        existingProduct.setLatestUnitPrice(productRequest.getLatestUnitPrice());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(String id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product updateStock(String id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        Product product = getProductById(id);
        product.setRemainingQuantity(quantity);
        return productRepository.save(product);
    }

    private String requireTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }

}
