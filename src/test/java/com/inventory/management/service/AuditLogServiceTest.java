package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.AuditLog;
import com.inventory.management.repository.AuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private AuditLogService service;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId("t1");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void logAction_populatesTenantAndFields() {
        when(repository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));
        AuditLog log = service.logAction(
                "Product", "P001", AuditLog.ActionType.UPDATE.value,
                "Updated product", Map.of("price", 10), Map.of("price", 12));

        assertThat(log.getTenantId()).isEqualTo("t1");
        assertThat(log.getEntityType()).isEqualTo("Product");
        assertThat(log.getActionType()).isEqualTo(AuditLog.ActionType.UPDATE.value);
        verify(repository).save(any(AuditLog.class));
    }
}
