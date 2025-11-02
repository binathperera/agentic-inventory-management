package com.inventory.management.service;

import com.inventory.management.dto.ProductRequest;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Product;
import com.inventory.management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getProductsBySupplier(String supplier) {
        return productRepository.findBySupplier(supplier);
    }
    
    public Product createProduct(ProductRequest productRequest) {
        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new RuntimeException("Product with SKU " + productRequest.getSku() + " already exists");
        }
        
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setSku(productRequest.getSku());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setSupplier(productRequest.getSupplier());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(String id, ProductRequest productRequest) {
        Product product = getProductById(id);
        
        // Note: SKU is immutable and cannot be changed after product creation
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setSupplier(productRequest.getSupplier());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(String id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    
    public Product updateStock(String id, Integer quantity) {
        Product product = getProductById(id);
        product.setQuantity(quantity);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}
