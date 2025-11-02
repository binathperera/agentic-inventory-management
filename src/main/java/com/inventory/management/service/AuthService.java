package com.inventory.management.service;

import com.inventory.management.dto.JwtResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.MessageResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
        
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new JwtResponse(jwt, userDetails.getUsername(), user.getEmail(), roles);
    }
    
    public MessageResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        Set<String> roles = signupRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        return new MessageResponse("User registered successfully!");
    }
}
