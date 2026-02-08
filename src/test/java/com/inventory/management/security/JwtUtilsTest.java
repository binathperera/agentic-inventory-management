package com.inventory.management.security;

import com.inventory.management.dto.Role;
import com.inventory.management.dto.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
            "TestSecretKeyForJwtTokenGenerationAndValidation1234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    void testGenerateJwtToken() {
        
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "u1",           
            "testuser",     
            "test@example.com", 
            "tenant1",      
            roles         
        );
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT format check
    }

    @Test
    void testGetUserNameFromJwtToken() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "u1", "testuser", "test@example.com", "tenant1", roles
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testGetTenantIdFromJwtToken() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "u1", "testuser", "test@example.com", "tenant1", roles
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(authentication);
        String tenantId = jwtUtils.getTenantIdFromJwtToken(token);

        assertEquals("tenant1", tenantId);
    }

    @Test
    void testValidateJwtToken() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "u1", "testuser", "test@example.com", "tenant1", roles
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(authentication);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateInvalidJwtToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    void testGetRolesFromJwtToken() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Role cashierRole = new Role();
        cashierRole.setName("CASHIER");
        roles.add(adminRole);
        roles.add(cashierRole);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
            "u1", "testuser", "test@example.com", "tenant1", roles
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
    }
}

