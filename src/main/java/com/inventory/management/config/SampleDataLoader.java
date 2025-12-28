package com.inventory.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.inventory.management.model.Supplier;
import com.inventory.management.model.Product;
import com.inventory.management.model.Tenant;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.SupplierRepository;
import com.inventory.management.repository.TenantRepository;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public void run(String... args) throws Exception {
        // Checking if data already exists
        Tenant tenant1 = null, tenant2 = null;
        if (tenantRepository.count() == 0) {
            tenant1 = tenantRepository.save(new Tenant("ABC Corporation", "abc"));
            tenant2 = tenantRepository.save(new Tenant("XYZ Enterprises", "xyz"));
        } else {
            tenant1 = tenantRepository.findAll().get(0);
        }
        if (tenant1 == null) {
            return;
        }
        if (supplierRepository.count() == 0) {
            supplierRepository.save(
                    new Supplier(tenant1.getId(), "Supplier A", "supplierA@example.com", "0771234567", "Colombo"));
            supplierRepository
                    .save(new Supplier(tenant1.getId(), "Supplier B", "supplierB@example.com", "0772345678", "Galle"));
            supplierRepository
                    .save(new Supplier(tenant1.getId(), "Supplier C", "supplierC@example.com", "0773456789", "Kandy"));
        }

        if (productRepository.count() == 0) {
            // Add sample products if needed
            productRepository.save(new Product(tenant1.getId(), "P001", "Product 1", "BATCH001", 100, 10.5f));
            productRepository.save(new Product(tenant1.getId(), "P002", "Product 2", "BATCH002", 200, 20.0f));
            productRepository.save(new Product(tenant1.getId(), "P003", "Product 3", "BATCH003", 150, 15.75f));
        }
    }
}
