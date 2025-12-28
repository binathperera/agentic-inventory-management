package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.Role;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users for the current tenant
     */
    public List<User> getAllUsers() {
        String tenantId = requireTenantId();
        return userRepository.findAll().stream()
                .filter(user -> tenantId.equals(user.getTenantId()))
                .toList();
    }

    /**
     * Get user by ID (must belong to current tenant)
     */
    public User getUserById(String id) {
        String tenantId = requireTenantId();
        return userRepository.findById(id)
                .filter(user -> tenantId.equals(user.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Get user by username in current tenant
     */
    public User getUserByUsername(String username) {
        String tenantId = requireTenantId();
        return userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user by email in current tenant
     */
    public User getUserByEmail(String email) {
        String tenantId = requireTenantId();
        return userRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Create a new user in the current tenant
     */
    public User createUser(String username, String email, String password, Set<Role> roles) {
        String tenantId = requireTenantId();

        // Validate input
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Check if username or email already exists for this tenant
        if (userRepository.existsByUsernameAndTenantId(username, tenantId)) {
            throw new IllegalArgumentException("Username already exists for this tenant");
        }
        if (userRepository.existsByEmailAndTenantId(email, tenantId)) {
            throw new IllegalArgumentException("Email already exists for this tenant");
        }

        User user = new User(tenantId, username, email, passwordEncoder.encode(password));
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        } else {
            user.setRoles(Set.of(new Role("ROLE_USER", "Default user role", 1, null)));
        }
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Update user information (except password)
     */
    public User updateUser(String id, String email, Set<Role> roles) {
        User user = getUserById(id);

        if (email != null && !email.isEmpty()) {
            // Check if email already exists for another user in this tenant
            String tenantId = requireTenantId();
            if (!email.equals(user.getEmail()) && userRepository.existsByEmailAndTenantId(email, tenantId)) {
                throw new IllegalArgumentException("Email already exists for this tenant");
            }
            user.setEmail(email);
        }

        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public User changePassword(String id, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Enable/disable user
     */
    public User setUserEnabled(String id, boolean enabled) {
        User user = getUserById(id);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    /**
     * Delete user (only for current tenant)
     */
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /**
     * Add role to user
     */
    public User addRoleToUser(String id, Role role) {
        User user = getUserById(id);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    /**
     * Remove role from user
     */
    public User removeRoleFromUser(String id, Role role) {
        User user = getUserById(id);
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    /**
     * Check if username exists in current tenant
     */
    public boolean existsByUsername(String username) {
        String tenantId = requireTenantId();
        return userRepository.existsByUsernameAndTenantId(username, tenantId);
    }

    /**
     * Check if email exists in current tenant
     */
    public boolean existsByEmail(String email) {
        String tenantId = requireTenantId();
        return userRepository.existsByEmailAndTenantId(email, tenantId);
    }

    /**
     * Get tenant ID from context
     */
    private String requireTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }
}
