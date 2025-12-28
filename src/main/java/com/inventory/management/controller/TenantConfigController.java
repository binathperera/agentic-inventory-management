package com.inventory.management.controller;

import com.inventory.management.model.TenantConfig;
import com.inventory.management.service.TenantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenant-config")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantConfigController {

    @Autowired
    private TenantConfigService tenantConfigService;

    /**
     * Get tenant config by subdomain (public endpoint for frontend initialization)
     * 
     * @param subDomain The subdomain to lookup
     * @return TenantConfig
     */
    @GetMapping("/by-subdomain/{subDomain}")
    public ResponseEntity<TenantConfig> getConfigBySubDomain(@PathVariable String subDomain) {
        return ResponseEntity.ok(tenantConfigService.getConfigBySubDomain(subDomain));
    }

    /**
     * Get current tenant's config (requires authentication)
     * 
     * @return TenantConfig for authenticated user's tenant
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TenantConfig> getCurrentTenantConfig() {
        return ResponseEntity.ok(tenantConfigService.getCurrentTenantConfig());
    }

    /**
     * Update current tenant's config (admin only)
     * 
     * @param tenantConfig Updated config data
     * @return Updated TenantConfig
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantConfig> updateConfig(@RequestBody TenantConfig tenantConfig) {
        return ResponseEntity.ok(tenantConfigService.updateConfig(tenantConfig));
    }

    /**
     * Create default config for current tenant (admin only)
     * 
     * @return Created TenantConfig
     */
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantConfig> initializeConfig() {
        String tenantId = com.inventory.management.config.TenantContext.getTenantId();
        return ResponseEntity.ok(tenantConfigService.createDefaultConfig(tenantId));
    }

    /**
     * Health check for specific branding fields
     * 
     * @return Map with basic branding info
     */
    @GetMapping("/branding")
    public ResponseEntity<Map<String, Object>> getBranding() {
        TenantConfig config = tenantConfigService.getCurrentTenantConfig();
        TenantConfig.Brand brand = config.getBrand();

        return ResponseEntity.ok(Map.of(
                "name", brand.getName() != null ? brand.getName() : "",
                "logo_url", brand.getLogoUrl() != null ? brand.getLogoUrl() : "",
                "primary_color", brand.getPrimaryColor() != null ? brand.getPrimaryColor() : "",
                "secondary_color", brand.getSecondaryColor() != null ? brand.getSecondaryColor() : ""));
    }
}
