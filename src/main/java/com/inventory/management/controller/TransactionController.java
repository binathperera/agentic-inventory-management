package com.inventory.management.controller;

import com.inventory.management.model.Transaction;
import com.inventory.management.model.TransactionItem;
import com.inventory.management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.create(transaction));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Transaction>> listAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Transaction> getById(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getByTransactionId(transactionId));
    }

    @GetMapping("/outstanding")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Transaction>> outstanding() {
        return ResponseEntity.ok(transactionService.getOutstanding());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Transaction>> byDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(transactionService.getByDateRange(start, end));
    }

    // Items
    @GetMapping("/{transactionId}/items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<TransactionItem>> getItems(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getItems(transactionId));
    }

    @PostMapping("/{transactionId}/items")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TransactionItem> addItem(@PathVariable String transactionId,
            @RequestBody TransactionItem item) {
        return ResponseEntity.ok(transactionService.addItem(transactionId, item));
    }

    @DeleteMapping("/{transactionId}/items/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Void> deleteItem(@PathVariable String transactionId, @PathVariable String productId) {
        transactionService.removeItem(transactionId, productId);
        return ResponseEntity.noContent().build();
    }
}