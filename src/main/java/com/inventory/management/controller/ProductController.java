package com.inventory.management.controller;

import com.inventory.management.dto.ProductRequest;
import com.inventory.management.model.Product;
import com.inventory.management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
    
    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<Product>> getProductsBySupplier(@PathVariable String supplier) {
        return ResponseEntity.ok(productService.getProductsBySupplier(supplier));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, 
                                                   @Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
    
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Product> updateStock(@PathVariable String id, 
                                                 @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }
}
