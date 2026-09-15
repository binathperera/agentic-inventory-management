package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.Product;
import com.inventory.management.model.ProductBatch;
import com.inventory.management.repository.ProductBatchRepository;
import com.inventory.management.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductBatchServiceTest {

    @Mock
    private ProductBatchRepository repository;

    @Mock
    private ProductRepository productRepository;

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
        batch.setProductId("P001");
        batch.setQty(10);
        Product product = product("P001", 5);
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));
        when(repository.save(any(ProductBatch.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductBatch saved = service.create(batch);
        assertThat(saved.getTenantId()).isEqualTo("t1");
        assertThat(product.getRemainingQuantity()).isEqualTo(15);
        verify(repository).save(any(ProductBatch.class));
        verify(productRepository).save(product);
    }

    @Test
    void getExpiringBefore_callsRepository() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        service.getExpiringBefore(date);
        verify(repository).findByTenantIdAndExpBefore("t1", date);
    }

    @Test
    void update_updatesTenantScopedBatch() {
        ProductBatch existing = new ProductBatch();
        existing.setId("batch-1");
        existing.setTenantId("t1");
        existing.setProductId("P001");
        existing.setQty(10);
        ProductBatch changes = new ProductBatch();
        changes.setProductId("P001");
        changes.setInvoiceNo("INV-2");
        changes.setBatchNo("BATCH-2");
        changes.setQty(25);
        changes.setUnitCost(8f);
        changes.setUnitPrice(12f);
        changes.setExp(LocalDate.of(2027, 1, 1));
        Product product = product("P001", 20);
        when(repository.findById("batch-1")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));

        ProductBatch result = service.update("batch-1", changes);

        assertThat(result.getProductId()).isEqualTo("P001");
        assertThat(result.getQty()).isEqualTo(25);
        assertThat(product.getRemainingQuantity()).isEqualTo(35);
        verify(repository).save(existing);
    }

    @Test
    void delete_deletesTenantScopedBatch() {
        ProductBatch existing = new ProductBatch();
        existing.setTenantId("t1");
        existing.setProductId("P001");
        existing.setQty(10);
        Product product = product("P001", 20);
        when(repository.findById("batch-1")).thenReturn(Optional.of(existing));
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));

        service.delete("batch-1");

        verify(repository).delete(existing);
        assertThat(product.getRemainingQuantity()).isEqualTo(10);
        verify(productRepository).save(product);
    }

    private Product product(String id, int quantity) {
        Product product = new Product();
        product.setId(id);
        product.setTenantId("t1");
        product.setRemainingQuantity(quantity);
        return product;
    }
}
