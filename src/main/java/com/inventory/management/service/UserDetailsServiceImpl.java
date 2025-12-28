package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.model.User;
import com.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDetails loadUserByTenantIdAndUsername(String tenantId, String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndTenant_id(username, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username + " for tenant: " + tenantId));
        
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Grab the tenant_id resolved from the subdomain by your Filter
        String tenantId = TenantContext.getTenantId();
        
        if (tenantId == null) {
            throw new UsernameNotFoundException("Access denied: No tenant identified.");
        }

        User user = userRepository.findByUsernameAndTenant_id(username, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}
