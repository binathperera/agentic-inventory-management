package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.TenantConfig;
import com.inventory.management.repository.TenantConfigRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantConfigService {

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private TenantService tenantService;

    /**
     * Get tenant config by subdomain
     * 
     * @param subDomain The subdomain to lookup
     * @return TenantConfig for the tenant
     */
    public TenantConfig getConfigBySubDomain(String subDomain) {
        String tenantId = tenantService.getTenantIdBySubDomain(subDomain);
        System.out.println("Resolved tenantId: " + tenantId + " for subDomain: " + subDomain);
        if (tenantId == null) {
            throw new ResourceNotFoundException("Tenant not found for subdomain: " + subDomain);
        }
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByTenantId(tenantId);
        if (tenantConfig.isEmpty()) {
            System.out.println("No config found for tenantId: " + tenantId);
            return createDefaultConfig(tenantId);
        } else {
            return tenantConfig.get();
        }
    }

    /**
     * Get tenant config for current tenant from context
     * 
     * @return TenantConfig for the current tenant
     */
    public TenantConfig getCurrentTenantConfig() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found for tenant: " + tenantId));
    }

    /**
     * Get tenant config by tenant ID
     * 
     * @param tenantId The tenant ID
     * @return TenantConfig for the tenant
     */
    public TenantConfig getConfigByTenantId(String tenantId) {
        return tenantConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found for tenant: " + tenantId));
    }

    /**
     * Create or update tenant config
     * 
     * @param tenantConfig The tenant config to save
     * @return Saved TenantConfig
     */
    public TenantConfig saveConfig(TenantConfig tenantConfig) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        // Ensure the config belongs to the current tenant
        tenantConfig.setTenantId(tenantId);
        return tenantConfigRepository.save(tenantConfig);
    }

    /**
     * Update existing tenant config
     * 
     * @param tenantConfig Updated tenant config data
     * @return Updated TenantConfig
     */
    public TenantConfig updateConfig(TenantConfig tenantConfig) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }

        // Fetch existing config
        TenantConfig existingConfig = getCurrentTenantConfig();

        // Update fields
        if (tenantConfig.getBrand() != null) {
            existingConfig.setBrand(tenantConfig.getBrand());
        }
        if (tenantConfig.getUiTheme() != null) {
            existingConfig.setUiTheme(tenantConfig.getUiTheme());
        }
        if (tenantConfig.getLocalization() != null) {
            existingConfig.setLocalization(tenantConfig.getLocalization());
        }
        if (tenantConfig.getFeatures() != null) {
            existingConfig.setFeatures(tenantConfig.getFeatures());
        }

        return tenantConfigRepository.save(existingConfig);
    }

    /**
     * Create default config for a new tenant
     * 
     * @param tenantId The tenant ID
     * @return Created TenantConfig with default values
     */
    public TenantConfig createDefaultConfig(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank");
        }

        if (tenantConfigRepository.existsByTenantId(tenantId)) {
            // If config already exists, return it instead of throwing error
            return tenantConfigRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Config not found for tenant: " + tenantId));
        }

        TenantConfig config = new TenantConfig();
        config.setTenantId(tenantId);

        // Set default brand
        TenantConfig.Brand brand = new TenantConfig.Brand();
        brand.setName("Default Brand");
        brand.setPrimaryColor("#1976d2");
        brand.setSecondaryColor("#dc004e");
        brand.setFontFamily("Roboto, sans-serif");
        config.setBrand(brand);

        // Set default UI theme
        TenantConfig.UiTheme uiTheme = new TenantConfig.UiTheme();
        uiTheme.setMode("light");
        uiTheme.setAccentColor("#f50057");
        uiTheme.setLayoutStyle("comfortable");
        uiTheme.setCornerStyle("rounded");
        config.setUiTheme(uiTheme);

        // Set default localization
        TenantConfig.Localization localization = new TenantConfig.Localization();
        localization.setLanguage("en");
        localization.setTimezone("UTC");
        localization.setCurrency("USD");
        localization.setDateFormat("MM/DD/YYYY");
        config.setLocalization(localization);

        // Set default features (all enabled)
        TenantConfig.Features features = new TenantConfig.Features();
        features.setInventoryModule(true);
        features.setReportingModule(true);
        features.setSupplierManagement(true);
        features.setAdvancedPricing(true);
        config.setFeatures(features);

        return tenantConfigRepository.save(config);
    }
}
