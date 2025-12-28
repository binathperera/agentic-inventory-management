package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Transaction;
import com.inventory.management.model.TransactionItem;
import com.inventory.management.repository.TransactionItemRepository;
import com.inventory.management.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    private String tenant() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }

    public List<Transaction> getAll() {
        return transactionRepository.findByTenantId(tenant());
    }

    public Transaction getByTransactionId(String transactionId) {
        return transactionRepository.findByTenantIdAndTransactionId(tenant(), transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }

    public List<Transaction> getOutstanding() {
        return transactionRepository.findByTenantIdAndBalanceAmountGreaterThan(tenant(), 0.0);
    }

    public List<Transaction> getByDateRange(Instant start, Instant end) {
        return transactionRepository.findByTenantIdAndCreatedAtBetween(tenant(), start, end);
    }

    public Transaction create(Transaction transaction) {
        transaction.setTenantId(tenant());
        return transactionRepository.save(transaction);
    }

    // Items
    public List<TransactionItem> getItems(String transactionId) {
        return transactionItemRepository.findByTenantIdAndTransactionId(tenant(), transactionId);
    }

    public TransactionItem addItem(String transactionId, TransactionItem item) {
        item.setTenantId(tenant());
        item.setTransactionId(transactionId);
        return transactionItemRepository.save(item);
    }

    public boolean removeItem(String transactionId, String itemId) {
        String tenantId = tenant();
        TransactionItem item = transactionItemRepository.findByTenantIdAndTransactionIdAndProductId(tenantId, transactionId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction item not found: " + itemId));
        transactionItemRepository.delete(item);
        return true;
    }

}
