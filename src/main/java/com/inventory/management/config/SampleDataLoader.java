package com.inventory.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.inventory.management.model.Supplier;
import com.inventory.management.repository.SupplierRepository;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        // Checking if data already exists
        if (supplierRepository.count() == 0) {
            supplierRepository.save(new Supplier("Supplier A", "supplierA@example.com", "0771234567", "Colombo"));
            supplierRepository.save(new Supplier("Supplier B", "supplierB@example.com", "0772345678", "Galle"));
            supplierRepository.save(new Supplier("Supplier C", "supplierC@example.com", "0773456789", "Kandy"));
            
        }
    }
}
