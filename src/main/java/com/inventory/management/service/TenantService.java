package com.inventory.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.Tenant;
import com.inventory.management.repository.TenantRepository;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Get tenant ID by subdomain
     */
    public String getTenantIdBySubDomain(String subDomain) {
        Tenant tenant = tenantRepository.findBySubDomain(subDomain).orElse(null);
        return tenant != null ? tenant.getId() : null;
    }

    /**
     * Get all tenants
     */
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    /**
     * Get tenant by ID
     */
    public Tenant getTenantById(String id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    /**
     * Get tenant by subdomain
     */
    public Tenant getTenantBySubDomain(String subDomain) {
        return tenantRepository.findBySubDomain(subDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with subdomain: " + subDomain));
    }

    /**
     * Create a new tenant
     */
    public Tenant createTenant(String name, String subDomain) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tenant name cannot be null or empty");
        }
        if (subDomain == null || subDomain.isEmpty()) {
            throw new IllegalArgumentException("Subdomain cannot be null or empty");
        }

        // Check if subdomain already exists
        if (tenantRepository.findBySubDomain(subDomain).isPresent()) {
            throw new IllegalArgumentException("Subdomain already exists: " + subDomain);
        }

        Tenant tenant = new Tenant(name, subDomain);
        return tenantRepository.save(tenant);
    }

    /**
     * Update tenant information
     */
    public Tenant updateTenant(String id, String name, String subDomain) {
        Tenant tenant = getTenantById(id);

        if (name != null && !name.isEmpty()) {
            tenant.setName(name);
        }

        if (subDomain != null && !subDomain.isEmpty()) {
            // Check if new subdomain is already taken by another tenant
            if (!subDomain.equals(tenant.getSubDomain()) && tenantRepository.findBySubDomain(subDomain).isPresent()) {
                throw new IllegalArgumentException("Subdomain already exists: " + subDomain);
            }
            tenant.setSubDomain(subDomain);
        }

        return tenantRepository.save(tenant);
    }

    /**
     * Delete tenant by ID
     */
    public void deleteTenant(String id) {
        Tenant tenant = getTenantById(id);
        tenantRepository.delete(tenant);
    }

    /**
     * Check if tenant exists by ID
     */
    public boolean existsById(String id) {
        return tenantRepository.existsById(id);
    }

    /**
     * Check if subdomain exists
     */
    public boolean existsBySubDomain(String subDomain) {
        return tenantRepository.findBySubDomain(subDomain).isPresent();
    }
}
