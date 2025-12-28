package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.ProductBatch;
import com.inventory.management.repository.ProductBatchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductBatchServiceTest {

    @Mock
    private ProductBatchRepository repository;

    @InjectMocks
    private ProductBatchService service;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId("t1");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void getByInvoice_returnsTenantScopedBatches() {
        List<ProductBatch> expected = List.of(new ProductBatch());
        when(repository.findByTenantIdAndInvoiceNo("t1", "INV-1")).thenReturn(expected);

        List<ProductBatch> result = service.getByInvoice("INV-1");
        assertThat(result).isEqualTo(expected);
        verify(repository).findByTenantIdAndInvoiceNo("t1", "INV-1");
    }

    @Test
    void create_setsTenantAndSaves() {
        ProductBatch batch = new ProductBatch();
        when(repository.save(any(ProductBatch.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductBatch saved = service.create(batch);
        assertThat(saved.getTenantId()).isEqualTo("t1");
        verify(repository).save(any(ProductBatch.class));
    }

    @Test
    void getExpiringBefore_callsRepository() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        service.getExpiringBefore(date);
        verify(repository).findByTenantIdAndExpBefore("t1", date);
    }
}
