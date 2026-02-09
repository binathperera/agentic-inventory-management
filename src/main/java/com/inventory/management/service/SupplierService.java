package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.SupplierDTO;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Supplier;
import com.inventory.management.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        String tenantId = requireTenantId();
        return supplierRepository.findAll().stream()
                .filter(supplier -> tenantId.equals(supplier.getTenantId()))
                .toList();
    }

    public Supplier getSupplierById(String id) {
        String tenantId = requireTenantId();
        return supplierRepository.findById(id)
                .filter(supplier -> tenantId.equals(supplier.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    public Supplier createSupplier(SupplierDTO supplierRequest) {
        String tenantId = requireTenantId();
        Supplier supplier = new Supplier(
                tenantId,
                supplierRequest.getName(),
                supplierRequest.getEmail(),
                supplierRequest.getContact(),
                supplierRequest.getAddress());
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(String id, SupplierDTO supplierRequest) {
        Supplier existingSupplier = getSupplierById(id);
        existingSupplier.setName(supplierRequest.getName());
        existingSupplier.setEmail(supplierRequest.getEmail());
        existingSupplier.setContact(supplierRequest.getContact());
        existingSupplier.setAddress(supplierRequest.getAddress());
        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(String id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    private String requireTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }
}
