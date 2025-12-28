package com.inventory.management.controller;

import com.inventory.management.model.Invoice;
import com.inventory.management.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Invoice>> listAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/{invoiceNo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Invoice> getByInvoice(@PathVariable String invoiceNo) {
        return ResponseEntity.ok(invoiceService.getByInvoiceNo(invoiceNo));
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Invoice>> bySupplier(@PathVariable String supplierId) {
        return ResponseEntity.ok(invoiceService.getBySupplier(supplierId));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Invoice>> byDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(invoiceService.getByDateRange(start, end));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> create(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.create(invoice));
    }
}
