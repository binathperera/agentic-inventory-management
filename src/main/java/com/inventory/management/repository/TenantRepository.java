package com.inventory.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.inventory.management.model.Tenant;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {

}
