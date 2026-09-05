package com.inventory.management.controller;

import com.inventory.management.dto.JwtResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.SignupRequest;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import com.inventory.management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import com.inventory.management.service.TenantService;
import com.inventory.management.config.TenantContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // Ensure tenant context is set for login attempts. Try TenantContext, then Host/Origin header.
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isBlank()) {
                // 1) Check for explicit tenant id header set by frontend
                String explicitTenantId = request.getHeader("X-Tenant-Id");
                if (explicitTenantId != null && !explicitTenantId.isBlank()) {
                    TenantContext.setTenantId(explicitTenantId);
                } else {
                    // 2) Check for tenant subdomain header provided by frontend
                    String subdomainHeader = request.getHeader("X-Tenant-Subdomain");
                    if (subdomainHeader != null && !subdomainHeader.isBlank()) {
                        String resolved = tenantService.getTenantIdBySubDomain(subdomainHeader.trim());
                        if (resolved != null && !resolved.isBlank()) {
                            TenantContext.setTenantId(resolved);
                        }
                    } else {
                        // 3) Try to extract subdomain from Host or Origin
                        String host = request.getHeader("Host");
                        if (host == null) host = request.getHeader("Origin");
                        if (host != null) {
                            host = host.replaceFirst("^https?://", "");
                            int slash = host.indexOf('/'); if (slash > -1) host = host.substring(0, slash);
                            if (host.contains(":")) host = host.split(":")[0];
                            String[] parts = host.split("\\.");
                            if (parts.length > 1 && !"localhost".equalsIgnoreCase(parts[0])) {
                                String subdomain = parts[0];
                                String resolved = tenantService.getTenantIdBySubDomain(subdomain);
                                if (resolved != null && !resolved.isBlank()) {
                                    TenantContext.setTenantId(resolved);
                                }
                            }
                        }
                    }
                }
            }

            // If still no tenant resolved, try to find the user globally and set tenant context automatically
            if (TenantContext.getTenantId() == null || TenantContext.getTenantId().isBlank()) {
                String username = loginRequest.getUsername();
                if (username != null && !username.isBlank()) {
                    Optional<User> maybeUser = userRepository.findAll().stream()
                            .filter(u -> username.equals(u.getUsername()))
                            .findFirst();
                    if (maybeUser.isPresent()) {
                        TenantContext.setTenantId(maybeUser.get().getTenantId());
                        System.out.println("Auto-resolved tenant for login by username: " + TenantContext.getTenantId());
                    }
                }
            }
        } catch (Exception e) {
            // don't block authentication; log and continue
            System.err.println("Tenant resolution during login failed: " + e.getMessage());
        }

        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
    
        JwtResponse jwtResponse = authService.registerUser(signupRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
