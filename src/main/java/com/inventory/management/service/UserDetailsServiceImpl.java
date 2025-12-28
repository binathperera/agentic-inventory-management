package com.inventory.management.service;

import com.inventory.management.config.TenantContext;
import com.inventory.management.dto.UserDetailsImpl;
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

        public UserDetailsImpl loadUserByTenantIdAndUsername(String tenantId, String username)
                        throws UsernameNotFoundException {
                User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: "
                                                + username + " for tenant: " + tenantId));

                Collection<GrantedAuthority> authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getId()))
                                .collect(Collectors.toList());

                return new UserDetailsImpl(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getTenantId(),
                                authorities);
        }

        @Override
        public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
                // 1. Grab the tenant_id resolved from the subdomain by your Filter
                String tenantId = TenantContext.getTenantId();

                if (tenantId == null) {
                        throw new UsernameNotFoundException("Access denied: No tenant identified.");
                }

                User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User Not Found with username: " + username));

                Collection<GrantedAuthority> authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getId()))
                                .collect(Collectors.toList());

                return new UserDetailsImpl(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getTenantId(),
                                authorities);
        }
}
