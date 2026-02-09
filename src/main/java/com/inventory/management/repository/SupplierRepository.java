package com.inventory.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.inventory.management.model.Supplier;

@Repository
public interface SupplierRepository extends MongoRepository<Supplier, String> {
}

