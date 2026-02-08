package com.inventory.management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private String extractSubdomain(HttpServletRequest request) {
        // Try Host header first (preferred), then Origin
        String hostHeader = request.getHeader("Host");
        String originHeader = request.getHeader("Origin");
        String host = hostHeader != null ? hostHeader : originHeader;
        if (host == null) return null;

        // Remove protocol if present
        host = host.replaceFirst("^https?://", "");
        // Strip path
        int slashIdx = host.indexOf('/');
        if (slashIdx > -1) host = host.substring(0, slashIdx);
        // Remove port
        if (host.contains(":")) host = host.split(":")[0];

        // Examples this handles:
        // tenant1.localhost, tenant1.example.com, localhost, example.com
        String[] parts = host.split("\\\\.");
        if (parts.length == 0) return null;
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
                // No valid JWT, try to resolve tenant from subdomain (for auth endpoints like
                // login)
                System.out.println("No valid JWT found, resolving tenant from subdomain");
                String subdomain = extractSubdomain(request);
                System.out.println("Extracted subdomain: " + subdomain);

                if (subdomain != null && !subdomain.isBlank()) {
                    String tenantId = tenantService.getTenantIdBySubDomain(subdomain);
                    System.out.println("Resolved tenant ID from subdomain: " + tenantId);
                    if (tenantId != null && !tenantId.isBlank()) {
                        TenantContext.setTenantId(tenantId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in JWT authentication filter: {}", e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
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
