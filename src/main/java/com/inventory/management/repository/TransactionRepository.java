package com.inventory.management.repository;

import com.inventory.management.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * Find all transactions for a specific tenant
     */
    List<Transaction> findByTenantId(String tenantId);

    /**
     * Find a transaction by tenant and transaction ID
     */
    Optional<Transaction> findByTenantIdAndTransactionId(String tenantId, String transactionId);

    /**
     * Find transactions by payment method
     */
    List<Transaction> findByTenantIdAndPaymentMethod(String tenantId, String paymentMethod);

    /**
     * Find transactions within a date range
     */
    List<Transaction> findByTenantIdAndCreatedAtBetween(String tenantId, Instant startDate, Instant endDate);

    /**
     * Find transactions with balance amount greater than zero (unpaid/partially
     * paid)
     */
    List<Transaction> findByTenantIdAndBalanceAmountGreaterThan(String tenantId, Double amount);

    /**
     * Find recent transactions ordered by date (descending)
     */
    List<Transaction> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    /**
     * Find transactions after a specific date
     */
    List<Transaction> findByTenantIdAndCreatedAtAfter(String tenantId, Instant date);

    /**
     * Find transactions before a specific date
     */
    List<Transaction> findByTenantIdAndCreatedAtBefore(String tenantId, Instant date);

    /**
     * Check if a transaction exists for tenant and transaction ID
     */
    Boolean existsByTenantIdAndTransactionId(String tenantId, String transactionId);

    /**
     * Find transactions by gross amount range
     */
    List<Transaction> findByTenantIdAndGrossAmountBetween(String tenantId, Double minAmount, Double maxAmount);
}