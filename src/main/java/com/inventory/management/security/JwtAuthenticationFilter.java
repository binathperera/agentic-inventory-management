package com.inventory.management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.inventory.management.config.TenantContext;
import com.inventory.management.service.TenantService;
import com.inventory.management.service.UserDetailsServiceImpl;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private TenantService tenantService;

    @Value("${app.tenant.default-subdomain:}")
    private String defaultTenantSubdomain;

    private String extractSubdomain(HttpServletRequest request) {
        // Try Host header first (preferred), then Origin
        String hostHeader = request.getHeader("Host");
        String originHeader = request.getHeader("Origin");
        String host = hostHeader != null ? hostHeader : originHeader;
        if (host == null)
            return null;

        // Remove protocol if present
        host = host.replaceFirst("^https?://", "");
        // Strip path
        int slashIdx = host.indexOf('/');
        if (slashIdx > -1)
            host = host.substring(0, slashIdx);
        // Remove port
        if (host.contains(":"))
            host = host.split(":")[0];

        // Examples this handles:
        // tenant1.localhost, tenant1.example.com, localhost, example.com
        String[] parts = host.split("\\.");
        if (parts.length == 0)
            return null;
        // If first part is 'localhost' or plain host, no subdomain
        if ("localhost".equalsIgnoreCase(parts[0]) || parts.length == 1) {
            return null;
        }
        // Otherwise first segment is the subdomain (tenant)
        return parts[0];
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Filter triggered for: " + request.getRequestURI());
        try {
            String jwt = parseJwt(request);
            System.out.println("JWT extracted: " + jwt);

            // If JWT is valid, extract tenant and user details from JWT
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                System.out.println("JWT is valid, extracting user details from token");
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String tenantId = jwtUtils.getTenantIdFromJwtToken(jwt);
                System.out.println("JWT validated. Username: " + username + ", TenantId: " + tenantId);

                if (tenantId != null && !tenantId.isBlank()) {
                    // Set tenant context
                    TenantContext.setTenantId(tenantId);

                    // Load user details with authorities/roles
                    try {
                        UserDetails userDetails = userDetailsServiceImpl.loadUserByTenantIdAndUsername(tenantId,
                                username);
                        System.out.println("User details loaded. Authorities: " + userDetails.getAuthorities());

                        // Create authentication token with loaded authorities
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Authentication set successfully");
                    } catch (Exception e) {
                        System.out.println("Failed to load user details: " + e.getMessage());
                        logger.error("Failed to load user details: {}", e.getMessage());
                    }
                }
            } else {
                // No valid JWT: resolve an explicit tenant header or the host subdomain.
                String tenantId = request.getHeader("X-Tenant-Id");
                if (tenantId == null || tenantId.isBlank()) {
                    String subdomain = request.getHeader("X-Tenant-Subdomain");
                    if (subdomain == null || subdomain.isBlank()) {
                        subdomain = extractSubdomain(request);
                    }
                    if ((subdomain == null || subdomain.isBlank()) && isLocalhost(request)) {
                        subdomain = defaultTenantSubdomain;
                    }
                    if (subdomain != null && !subdomain.isBlank()) {
                        tenantId = tenantService.getTenantIdBySubDomain(subdomain.trim());
                    }
                }
                if (tenantId != null && !tenantId.isBlank()) {
                    TenantContext.setTenantId(tenantId.trim());
                }
            }
        } catch (Exception e) {
            logger.error("Error in JWT authentication filter: {}", e.getMessage());
            e.printStackTrace();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private boolean isLocalhost(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            return false;
        }
        host = host.replaceFirst("^https?://", "");
        int slashIdx = host.indexOf('/');
        if (slashIdx > -1) {
            host = host.substring(0, slashIdx);
        }
        if (host.contains(":")) {
            host = host.substring(0, host.indexOf(':'));
        }
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) {
    // String path = request.getServletPath();
    // return path.startsWith("/api/auth/");
    // }
}
