package com.inventory.management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.inventory.management.dto.UserDetailsImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Use a test-specific secret key, not the production one
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "TestSecretKeyForJwtTokenGenerationAndValidation1234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    void testGenerateJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "u1", "testuser", "test@example.com", "password", "tenant1",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUserNameFromJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "u1", "testuser", "test@example.com", "password", "tenant1",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testValidateJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                "u1", "testuser", "test@example.com", "password", "tenant1",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateInvalidJwtToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }
}
