package com.inventory.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inventory.management.model.Tenant;
import com.inventory.management.repository.TenantRepository;

@Service
public class TenantService {
    
    @Autowired
    private TenantRepository tenantRepository;

    public String getTenantIdBySubDomain(String subDomain) {
        Tenant tenant = tenantRepository.findBySub_domain(subDomain).orElse(null);
        return tenant != null ? tenant.getId() : null;
    }
}
