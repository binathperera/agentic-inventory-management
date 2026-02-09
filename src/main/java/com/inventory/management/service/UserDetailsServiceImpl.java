package com.inventory.management.service;

import com.inventory.management.dto.Role;
import com.inventory.management.dto.UserDetailsImpl;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Authentication entrypoint uses this method. Use TenantContext to resolve tenant
        String tenantId = com.inventory.management.config.TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new UsernameNotFoundException("Tenant id is not set in context");
        }

        return loadUserByTenantIdAndUsername(tenantId, username);
    }

    public UserDetails loadUserByTenantIdAndUsername(String tenantId, String username) {
        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: tenantId=" + tenantId + ", username=" + username));

        return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getTenantId(),
            user.getRoles(),
            user.getPassword(),
            user.isEnabled()
        );
    }
}
