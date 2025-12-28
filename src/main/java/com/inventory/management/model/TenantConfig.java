package com.inventory.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tenant_config")
public class TenantConfig {
    
    @Id
    private String id;
    
    @Field("tenant_id")
    private String tenantId;
    
    @Field("brand")
    private Brand brand;
    
    @Field("ui_theme")
    private UiTheme uiTheme;
    
    @Field("localization")
    private Localization localization;
    
    @Field("features")
    private Features features;
    
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
    
    @Field("schema_version")
    private int schemaVersion = 1;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Brand {
        private String name;
        
        @Field("logo_url")
        private String logoUrl;
        
        @Field("favicon_url")
        private String faviconUrl;
        
        @Field("banner_url")
        private String bannerUrl;
        
        @Field("primary_color")
        private String primaryColor;
        
        @Field("secondary_color")
        private String secondaryColor;
        
        @Field("font_family")
        private String fontFamily;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UiTheme {
        private String mode; // e.g., "light", "dark", "auto"
        
        @Field("accent_color")
        private String accentColor;
        
        @Field("layout_style")
        private String layoutStyle; // e.g., "compact", "comfortable", "spacious"
        
        @Field("corner_style")
        private String cornerStyle; // e.g., "rounded", "sharp", "smooth"
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Localization {
        private String language; // e.g., "en", "es", "fr"
        private String timezone; // e.g., "America/New_York", "UTC"
        private String currency; // e.g., "USD", "EUR", "GBP"
        
        @Field("date_format")
        private String dateFormat; // e.g., "MM/DD/YYYY", "DD/MM/YYYY"
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Features {
        @Field("inventory_module")
        private Boolean inventoryModule;
        
        @Field("reporting_module")
        private Boolean reportingModule;
        
        @Field("supplier_management")
        private Boolean supplierManagement;
        
        @Field("advanced_pricing")
        private Boolean advancedPricing;
    }
}
