package com.inventory.management.controller;

import com.inventory.management.dto.JwtResponse;
import com.inventory.management.dto.LoginRequest;
import com.inventory.management.dto.MessageResponse;
import com.inventory.management.dto.SignupRequest;
import com.inventory.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse messageResponse = authService.registerUser(signupRequest);
        return ResponseEntity.ok(messageResponse);
    }
}
