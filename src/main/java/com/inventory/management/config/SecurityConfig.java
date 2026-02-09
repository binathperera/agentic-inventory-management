package com.inventory.management.config;

import com.inventory.management.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/tenant-config/by-subdomain/**").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("GET", "/api/products/**").hasAnyRole("USER", "MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/products/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("PUT", "/api/products/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("PATCH", "/api/products/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("DELETE", "/api/products/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("GET", "/api/suppliers/**").hasAnyRole("USER", "MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/suppliers/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("PUT", "/api/suppliers/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("DELETE", "/api/suppliers/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("GET", "/api/invoices/**").hasAnyRole("USER", "MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/invoices/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("PUT", "/api/invoices/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("DELETE", "/api/invoices/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("GET", "/api/product-batches/**").hasAnyRole("USER", "MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/product-batches/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("PUT", "/api/product-batches/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("DELETE", "/api/product-batches/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/api/transactions/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("/api/chat/**").hasAnyRole("MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("GET", "/api/audit-logs/history/**").hasAnyRole("USER", "MANAGER", "CASHIER", "ADMIN")
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                .requestMatchers("GET", "/api/tenant-config/**").permitAll()
                .requestMatchers("/api/tenant-config/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

