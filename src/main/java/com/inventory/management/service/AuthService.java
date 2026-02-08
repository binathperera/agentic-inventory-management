package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.JwtResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.Role;
import com.inventory.management.dto.SignupRequest;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import com.inventory.management.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            System.out.println("Authenticating user: " + loginRequest.getUsername());
            
            // 1. Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            
            System.out.println("Authentication successful for user: " + loginRequest.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 2. Generate JWT with roles
            String jwt = jwtUtils.generateJwtToken(authentication);
            System.out.println("Generated JWT: " + jwt.substring(0, 20) + "...");
            
            // 3. Get tenant context (set by JwtAuthenticationFilter)
            String tenantId = TenantContext.getTenantId();
            System.out.println("Authenticating user for tenant: " + tenantId);
            
            if (tenantId == null) {
                throw new RuntimeException("Access denied: No tenant identified.");
            }
            
            // 4. Load full user details with tenant filtering
            User user = userRepository.findByTenantIdAndUsername(tenantId, loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found for tenant: " + tenantId));
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
            
            System.out.println("User roles: " + roles);
            
            return new JwtResponse(
                jwt, 
                user.getUsername(), 
                user.getEmail(), 
                user.getTenantId(),
                roles
            );
            
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return new JwtResponse("Error: " + e.getMessage());
        }
    }

    public JwtResponse registerUser(SignupRequest signupRequest) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Access denied: No tenant identified.");
        }

        // Check if user already exists
        if (userRepository.existsByTenantIdAndUsername(tenantId, signupRequest.getUsername())) {
            return new JwtResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByTenantIdAndEmail(tenantId, signupRequest.getEmail())) {
            return new JwtResponse("Error: Email is already in use!");
        }

        // Create user
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        User user = new User(tenantId, signupRequest.getUsername(), signupRequest.getEmail(), encodedPassword);
        
        // Set roles (default to CASHIER for new registrations)
        Set<Role> roles = signupRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            Role cashierRole = new Role();
            cashierRole.setName("CASHIER");
            roles = new HashSet<>();
            roles.add(cashierRole);
        }
        
        user.setRoles(roles);
        System.out.println("Registering user '" + signupRequest.getUsername() + "' for tenant: " + tenantId);
        
        User savedUser = userRepository.save(user);
        System.out.println("User registered successfully with ID: " + savedUser.getId());
        
        // Auto-login after registration
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(savedUser.getUsername());
        loginRequest.setPassword(signupRequest.getPassword()); // Plaintext for auto-login
        
        return authenticateUser(loginRequest);
    }
}
