package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.JwtResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.SignupRequest;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import com.inventory.management.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.inventory.management.model.Role;

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
            
        // 1. Grab the tenant_id resolved from the subdomain by your Filter
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new UsernameNotFoundException("Access denied: No tenant identified.");
        }
        User user = userRepository.findByUsernameAndTenant_id(userDetails.getUsername(), tenantId).orElseThrow();
        // if user not found, return error message
        if (user == null) {
            return new JwtResponse("Error: User not found!");
        }
        return new JwtResponse(jwt, userDetails.getUsername(), user.getEmail(), roles);
    }

    public JwtResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new JwtResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new JwtResponse("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        Set<Role> roles = signupRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(new Role("ROLE_USER", "Default role", 1, null));
        }
        user.setRoles(roles);
        User u = userRepository.save(user);

        // Automatically authenticate the user after successful registration
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(u.getUsername());
        loginRequest.setPassword(u.getPassword());

        return authenticateUser(loginRequest);
    }
}
