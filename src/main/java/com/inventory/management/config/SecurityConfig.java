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
                // PUBLIC (No login required)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/tenant-config/by-subdomain/**").permitAll()
                
                // USER MANAGEMENT (ADMIN ONLY)
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // PRODUCTS (USER can only GET, CASHIER/ADMIN can modify)
                .requestMatchers("GET", "/api/products/**").hasAnyRole("USER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/products/**").hasAnyRole("CASHIER", "ADMIN")
                .requestMatchers("PUT", "/api/products/**").hasAnyRole("CASHIER", "ADMIN")
                .requestMatchers("PATCH", "/api/products/**").hasAnyRole("CASHIER", "ADMIN")
                .requestMatchers("DELETE", "/api/products/**").hasRole("ADMIN")
                
                // SUPPLIERS (USER can only GET, CASHIER/ADMIN can modify)
                .requestMatchers("GET", "/api/suppliers/**").hasAnyRole("USER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/suppliers/**").hasAnyRole("CASHIER", "ADMIN")
                .requestMatchers("PUT", "/api/suppliers/**").hasAnyRole("CASHIER", "ADMIN")
                .requestMatchers("DELETE", "/api/suppliers/**").hasRole("ADMIN")
                
                // INVOICES (USER can only GET, CASHIER/ADMIN can create/modify, ADMIN can delete)
                .requestMatchers("GET", "/api/invoices/**").hasAnyRole("USER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/invoices/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/invoices/**").hasRole("ADMIN")
                
                // PRODUCT BATCHES (USER can only GET, CASHIER/ADMIN can modify)
                .requestMatchers("GET", "/api/product-batches/**").hasAnyRole("USER", "CASHIER", "ADMIN")
                .requestMatchers("POST", "/api/product-batches/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/product-batches/**").hasRole("ADMIN")
                
                // TRANSACTIONS (CASHIER/ADMIN only - not for USER)
                .requestMatchers("/api/transactions/**").hasAnyRole("CASHIER", "ADMIN")
                
                // AI CHAT (CASHIER/ADMIN only - not for USER)
                .requestMatchers("/api/chat/**").hasAnyRole("CASHIER", "ADMIN")
                
                // AUDIT LOGS (ADMIN only, except specific entity history accessible to USER/CASHIER)
                .requestMatchers("GET", "/api/audit-logs/history/**").hasAnyRole("USER", "CASHIER", "ADMIN")
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                
                // TENANT CONFIG (GET is public, others ADMIN only)
                .requestMatchers("GET", "/api/tenant-config/**").permitAll()
                .requestMatchers("/api/tenant-config/**").hasRole("ADMIN")
                
                // Everything else requires authentication
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
