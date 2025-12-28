package com.inventory.management.controller;

import com.inventory.management.model.Tenant;
import com.inventory.management.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantController {

    @Autowired
    private TenantService tenantService;

    /**
     * Get all tenants (super admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    /**
     * Get tenant by ID (super admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> getTenantById(@PathVariable String id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    /**
     * Get tenant by subdomain (public endpoint for frontend initialization)
     */
    @GetMapping("/subdomain/{subDomain}")
    public ResponseEntity<Tenant> getTenantBySubDomain(@PathVariable String subDomain) {
        return ResponseEntity.ok(tenantService.getTenantBySubDomain(subDomain));
    }

    /**
     * Create a new tenant (super admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> createTenant(@RequestBody CreateTenantRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getSubDomain() == null || request.getSubDomain().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Tenant tenant = tenantService.createTenant(request.getName(), request.getSubDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    /**
     * Update tenant information (super admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> updateTenant(
            @PathVariable String id,
            @RequestBody UpdateTenantRequest request) {
        Tenant tenant = tenantService.updateTenant(id, request.getName(), request.getSubDomain());
        return ResponseEntity.ok(tenant);
    }

    /**
     * Delete tenant (super admin only)
     * WARNING: This will delete the tenant and all associated data
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTenant(@PathVariable String id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(Map.of("message", "Tenant deleted successfully"));
    }

    /**
     * Check if tenant exists by ID
     */
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkTenantExists(@PathVariable String id) {
        boolean exists = tenantService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Check if subdomain is available (public endpoint for registration)
     */
    @GetMapping("/check-subdomain/{subDomain}")
    public ResponseEntity<Map<String, Boolean>> checkSubDomainAvailable(@PathVariable String subDomain) {
        boolean exists = tenantService.existsBySubDomain(subDomain);
        return ResponseEntity.ok(Map.of("available", !exists));
    }

    // DTOs for request bodies
    @lombok.Data
    public static class CreateTenantRequest {
        private String name;
        private String subDomain;
    }

    @lombok.Data
    public static class UpdateTenantRequest {
        private String name;
        private String subDomain;
    }
}
