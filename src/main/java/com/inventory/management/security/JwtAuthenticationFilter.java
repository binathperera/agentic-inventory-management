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
        String host = request.getHeader("Origin"); // e.g., "tenant1.localhost:3000"
        System.out.println("Host header: " + host);
        if (host != null) {
            // Remove port if present
            String subdomain = host.split("\\.")[0];
            String[] parts = subdomain.split("//");

            // If it's tenant1.localhost, parts[0] is "tenant1"
            if (parts.length > 1) {
                return parts[1];
            } else {
                return parts[0];
            }
        }
        return null;
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
