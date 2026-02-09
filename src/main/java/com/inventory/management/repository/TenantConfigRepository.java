package com.inventory.management.repository;

import com.inventory.management.model.TenantConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantConfigRepository extends MongoRepository<TenantConfig, String> {
    Optional<TenantConfig> findByTenantId(String tenantId);
    Boolean existsByTenantId(String tenantId);
}
