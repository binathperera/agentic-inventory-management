package com.inventory.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.inventory.management.model.Supplier;
import com.inventory.management.model.Product;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.SupplierRepository;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // Checking if data already exists
        if (supplierRepository.count() == 0) {
            supplierRepository.save(new Supplier("Supplier A", "supplierA@example.com", "0771234567", "Colombo"));
            supplierRepository.save(new Supplier("Supplier B", "supplierB@example.com", "0772345678", "Galle"));
            supplierRepository.save(new Supplier("Supplier C", "supplierC@example.com", "0773456789", "Kandy"));
            
        }

        if (productRepository.count() == 0) {
            // Add sample products if needed
            productRepository.save(new Product("Product 1", "Description 1", "SKU001", 10.0f, 100, "Category1", "Supplier A", java.time.LocalDateTime.now()));
            productRepository.save(new Product("Product 2", "Description 2", "SKU002", 20.0f, 200, "Category2", "Supplier B", java.time.LocalDateTime.now()));
            productRepository.save(new Product("Product 3", "Description 3", "SKU003", 30.0f, 300, "Category1", "Supplier C", java.time.LocalDateTime.now()));

        }
    }
}
