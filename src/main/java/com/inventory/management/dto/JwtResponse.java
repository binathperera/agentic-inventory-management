package com.inventory.management.dto;

import java.util.List;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String username;
    private String email;
    private String tenantId;
    private List<String> roles;
    
    public JwtResponse(String token, String username, String email, String tenantId, List<String> roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.tenantId = tenantId;
        this.roles = roles;
    }
    
    // Error constructor
    public JwtResponse(String error) {
        this.token = null;
        this.username = null;
        this.email = null;
        this.tenantId = null;
        this.roles = null;
    }
}

