package com.inventory.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inventory.management.model.Supplier;
import com.inventory.management.repository.SupplierRepository;

import org.springframework.lang.NonNull;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable String id) {
        return supplierRepository.findById(id).orElseThrow();
    }

    @SuppressWarnings("null")
    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @PutMapping("/{id}")
    public Supplier updateSupplier(@PathVariable String id, @RequestBody Supplier supplier) {
        supplier.set_id(id);
        return supplierRepository.save(supplier);
    }


    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable @NonNull String id) {
        supplierRepository.deleteById(id);
    }
}
