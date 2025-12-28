package com.inventory.management.controller;

import com.inventory.management.dto.Role;
import com.inventory.management.model.User;
import com.inventory.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get all users for current tenant (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get user by ID (user can see own info or admin can see any)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).id == authentication.principal.id")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get user by username (admin only)
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * Get user by email (admin only)
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Create a new user (admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Set<Role> roles = request.getRoles() != null ? request.getRoles()
                : Set.of(new Role("ROLE_USER", "Default user role", 1, null));
        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword(), roles);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Update user information (admin or self)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request.getEmail(), request.getRoles());
        return ResponseEntity.ok(user);
    }

    /**
     * Change user password (user can change own, admin can change any)
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable String id,
            @RequestBody ChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        userService.changePassword(id, request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /**
     * Enable user (admin only)
     */
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> enableUser(@PathVariable String id) {
        User user = userService.setUserEnabled(id, true);
        return ResponseEntity.ok(user);
    }

    /**
     * Disable user (admin only)
     */
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> disableUser(@PathVariable String id) {
        User user = userService.setUserEnabled(id, false);
        return ResponseEntity.ok(user);
    }

    /**
     * Delete user (admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    /**
     * Add role to user (admin only)
     */
    @PostMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> addRoleToUser(
            @PathVariable String id,
            @PathVariable String role) {
        try {
            Role roleEnum = new Role("ROLE_" + role.toUpperCase(), "Default role", 1, null);
            User user = userService.addRoleToUser(id, roleEnum);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove role from user (admin only)
     */
    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> removeRoleFromUser(
            @PathVariable String id,
            @PathVariable String role) {
        try {
            Role roleEnum = new Role("ROLE_" + role.toUpperCase(), "Default role", 1, null);
            User user = userService.removeRoleFromUser(id, roleEnum);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if username exists (public, for registration validation)
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Check if email exists (public, for registration validation)
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // DTOs for request bodies
    @lombok.Data
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private Set<Role> roles;
    }

    @lombok.Data
    public static class UpdateUserRequest {
        private String email;
        private Set<Role> roles;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        private String newPassword;
    }
}
