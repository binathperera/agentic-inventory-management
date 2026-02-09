package com.inventory.management.repository;

import com.inventory.management.model.TransactionItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionItemRepository extends MongoRepository<TransactionItem, String> {

    /**
     * Find all transaction items for a specific tenant
     */
    List<TransactionItem> findByTenantId(String tenantId);

    /**
     * Find all items for a specific transaction
     */
    List<TransactionItem> findByTenantIdAndTransactionId(String tenantId, String transactionId);

    /**
     * Find all transactions containing a specific product
     */
    List<TransactionItem> findByTenantIdAndProductId(String tenantId, String productId);

    /**
     * Find a specific transaction item by tenant, transaction, and product
     */
    Optional<TransactionItem> findByTenantIdAndTransactionIdAndProductId(String tenantId, String transactionId,
            String productId);

    /**
     * Find transaction items by quantity greater than a value
     */
    List<TransactionItem> findByTenantIdAndQtyGreaterThan(String tenantId, Integer qty);

    /**
     * Find transaction items by unit price range
     */
    List<TransactionItem> findByTenantIdAndUnitPriceBetween(String tenantId, Double minPrice, Double maxPrice);


    /**
     * Check if a transaction item exists for tenant, transaction, and product
     */
    Boolean existsByTenantIdAndTransactionIdAndProductId(String tenantId, String transactionId, String productId);

    /**
     * Delete all items for a specific transaction
     */
    void deleteByTenantIdAndTransactionId(String tenantId, String transactionId);

    /**
     * Count items in a transaction
     */
    Long countByTenantIdAndTransactionId(String tenantId, String transactionId);
}
