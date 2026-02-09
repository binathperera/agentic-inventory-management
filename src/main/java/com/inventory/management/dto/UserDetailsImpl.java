package com.inventory.management.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {
    private String id;
    private String username;
    private String email;
    private String tenantId;
    private Set<Role> roles;
    private String password;
    private boolean enabled;

    public UserDetailsImpl(String id, String username, String email, String tenantId, Set<Role> roles) {
        this(id, username, email, tenantId, roles, "", true);
    }

    public UserDetailsImpl(String id, String username, String email, String tenantId, Set<Role> roles, String password, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.tenantId = tenantId;
        this.roles = roles != null ? roles : Set.of();
        this.password = password;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
            .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return enabled; }
}
