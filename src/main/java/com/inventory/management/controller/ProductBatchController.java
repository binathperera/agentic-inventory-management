package com.inventory.management.controller;

import com.inventory.management.model.ProductBatch;
import com.inventory.management.service.ProductBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/product-batches")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductBatchController {

    @Autowired
    private ProductBatchService productBatchService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductBatch>> listAll() {
        return ResponseEntity.ok(productBatchService.getAll());

    }

    @GetMapping("/invoice/{invoiceNo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductBatch>> byInvoice(@PathVariable String invoiceNo) {
        return ResponseEntity.ok(productBatchService.getByInvoice(invoiceNo));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductBatch>> byProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productBatchService.getByProduct(productId));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ProductBatch>> expiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before) {
        return ResponseEntity.ok(productBatchService.getExpiringBefore(before));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductBatch> create(@RequestBody ProductBatch batch) {
        return ResponseEntity.ok(productBatchService.create(batch));
    }
}
