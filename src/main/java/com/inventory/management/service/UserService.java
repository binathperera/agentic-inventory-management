package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.Role;
import com.inventory.management.enums.RoleEnum;
import com.inventory.management.exception.ResourceNotFoundException;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        String tenantId = requireTenantId();
        return userRepository.findAll().stream()
                .filter(user -> tenantId.equals(user.getTenantId()))
                .toList();
    }

    public User getUserById(String id) {
        String tenantId = requireTenantId();
        return userRepository.findById(id)
                .filter(user -> tenantId.equals(user.getTenantId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        String tenantId = requireTenantId();
        return userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public User getUserByEmail(String email) {
        String tenantId = requireTenantId();
        return userRepository.findByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User createUser(String username, String email, String password, Set<Role> roles) {
        String tenantId = requireTenantId();

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (userRepository.existsByUsernameAndTenantId(username, tenantId)) {
            throw new IllegalArgumentException("Username already exists for this tenant");
        }
        if (userRepository.existsByEmailAndTenantId(email, tenantId)) {
            throw new IllegalArgumentException("Email already exists for this tenant");
        }

        User user = new User(tenantId, username, email, passwordEncoder.encode(password));
        user.setRoles(roles != null && !roles.isEmpty() ? roles : Set.of(new Role("USER")));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User updateUser(String id, String email, Set<Role> roles) {
        User user = getUserById(id);

        if (email != null && !email.isEmpty()) {
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

    public User changePassword(String id, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User setUserEnabled(String id, boolean enabled) {
        User user = getUserById(id);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User addRoleToUser(String id, Role role) {
        User user = getUserById(id);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRoleFromUser(String id, Role role) {
        User user = getUserById(id);
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public void promoteUser(String id) {
        User user = getUserById(id);
        Role primaryRole = getPrimaryRole(user);
        String currentRoleName = primaryRole.getName();
        
        RoleEnum currentRole = RoleEnum.fromString(currentRoleName);
        RoleEnum nextRole = getNextHigherRole(currentRole);
        
        if (nextRole == null) {
            throw new IllegalStateException("User already has highest role: " + currentRoleName);
        }
        
        Set<Role> newRoles = user.getRoles().stream()
                .filter(role -> !role.getName().equals(currentRoleName))
                .collect(Collectors.toSet());
        newRoles.add(new Role(nextRole.name()));
        
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    public void demoteUser(String id) {
        User user = getUserById(id);
        Role primaryRole = getPrimaryRole(user);
        String currentRoleName = primaryRole.getName();
        
        RoleEnum currentRole = RoleEnum.fromString(currentRoleName);
        RoleEnum lowerRole = getNextLowerRole(currentRole);
        
        if (lowerRole == null) {
            throw new IllegalStateException("User already has lowest role: " + currentRoleName);
        }
        
        Set<Role> newRoles = user.getRoles().stream()
                .filter(role -> !role.getName().equals(currentRoleName))
                .collect(Collectors.toSet());
        newRoles.add(new Role(lowerRole.name()));
        
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    private Role getPrimaryRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalStateException("User has no roles");
        }
        return user.getRoles().iterator().next();
    }

    private RoleEnum getNextHigherRole(RoleEnum current) {
        return switch (current) {
            case USER -> RoleEnum.CASHIER;
            case CASHIER -> RoleEnum.MANAGER;
            case MANAGER -> RoleEnum.ADMIN;
            case ADMIN -> null;
            default -> null;
        };
    }

    private RoleEnum getNextLowerRole(RoleEnum current) {
        return switch (current) {
            case USER -> null;
            case MANAGER -> RoleEnum.CASHIER;
            case CASHIER -> RoleEnum.USER;
            case ADMIN -> RoleEnum.MANAGER;
            default -> null;
        };
    }

    public boolean existsByUsername(String username) {
        String tenantId = requireTenantId();
        return userRepository.existsByUsernameAndTenantId(username, tenantId);
    }

    public boolean existsByEmail(String email) {
        String tenantId = requireTenantId();
        return userRepository.existsByEmailAndTenantId(email, tenantId);
    }

    private String requireTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant id is not set in context");
        }
        return tenantId;
    }
}
