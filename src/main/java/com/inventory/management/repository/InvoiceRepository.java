package com.inventory.management.repository;

import com.inventory.management.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    /**
     * Find all invoices for a specific tenant
     */
    List<Invoice> findByTenantId(String tenantId);

    /**
     * Find an invoice by tenant and invoice number
     */
    Optional<Invoice> findByTenantIdAndInvoiceNo(String tenantId, String invoiceNo);

    /**
     * Find all invoices for a specific supplier within a tenant
     */
    List<Invoice> findByTenantIdAndSupplierId(String tenantId, String supplierId);

    /**
     * Find invoices by date range
     */
    List<Invoice> findByTenantIdAndDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);

    /**
     * Find invoices by date
     */
    List<Invoice> findByTenantIdAndDate(String tenantId, LocalDate date);

    /**
     * Find invoices for a supplier within a date range
     */
    List<Invoice> findByTenantIdAndSupplierIdAndDateBetween(String tenantId, String supplierId, LocalDate startDate,
            LocalDate endDate);

    /**
     * Find recent invoices ordered by date (descending)
     */
    List<Invoice> findByTenantIdOrderByDateDesc(String tenantId);

    /**
     * Find invoices after a specific date
     */
    List<Invoice> findByTenantIdAndDateAfter(String tenantId, LocalDate date);

    /**
     * Find invoices before a specific date
     */
    List<Invoice> findByTenantIdAndDateBefore(String tenantId, LocalDate date);

    /**
     * Check if an invoice exists for tenant and invoice number
     */
    Boolean existsByTenantIdAndInvoiceNo(String tenantId, String invoiceNo);
}
