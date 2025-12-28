package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Invoice;
import com.inventory.management.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private String tenant() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }

    public List<Invoice> getAll() {
        return invoiceRepository.findByTenantId(tenant());
    }

    public Invoice getByInvoiceNo(String invoiceNo) {
        return invoiceRepository.findByTenantIdAndInvoiceNo(tenant(), invoiceNo)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceNo));
    }

    public List<Invoice> getBySupplier(String supplierId) {
        return invoiceRepository.findByTenantIdAndSupplierId(tenant(), supplierId);
    }

    public List<Invoice> getByDateRange(LocalDate start, LocalDate end) {
        return invoiceRepository.findByTenantIdAndDateBetween(tenant(), start, end);
    }

    public Invoice create(Invoice invoice) {
        invoice.setTenantId(tenant());
        return invoiceRepository.save(invoice);
    }
}
