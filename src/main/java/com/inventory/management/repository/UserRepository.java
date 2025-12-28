package com.inventory.management.repository;

import com.inventory.management.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
    Optional<User> findByEmailAndTenantId(String email, String tenantId);
    Boolean existsByUsernameAndTenantId(String username, String tenantId);
    Boolean existsByEmailAndTenantId(String email, String tenantId);
}
