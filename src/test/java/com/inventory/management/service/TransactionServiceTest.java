package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Transaction;
import com.inventory.management.model.TransactionItem;
import com.inventory.management.repository.TransactionItemRepository;
import com.inventory.management.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionItemRepository itemRepository;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId("t1");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void getByTransactionId_returnsEntity() {
        Transaction txn = new Transaction();
        when(transactionRepository.findByTenantIdAndTransactionId("t1", "TXN-1")).thenReturn(Optional.of(txn));
        assertThat(service.getByTransactionId("TXN-1")).isSameAs(txn);
    }

    @Test
    void getByTransactionId_throwsWhenMissing() {
        when(transactionRepository.findByTenantIdAndTransactionId("t1", "TXN-404")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByTransactionId("TXN-404"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addItem_setsTenantAndTransaction() {
        TransactionItem item = new TransactionItem();
        when(itemRepository.save(any(TransactionItem.class))).thenAnswer(inv -> inv.getArgument(0));
        TransactionItem saved = service.addItem("TXN-1", item);
        assertThat(saved.getTenantId()).isEqualTo("t1");
        assertThat(saved.getTransactionId()).isEqualTo("TXN-1");
        verify(itemRepository).save(any(TransactionItem.class));
    }

    @Test
    void byDateRange_delegatesToRepository() {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-12-31T23:59:59Z");
        service.getByDateRange(start, end);
        verify(transactionRepository).findByTenantIdAndCreatedAtBetween("t1", start, end);
    }
}
