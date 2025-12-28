package com.inventory.management.controller;

import com.inventory.management.model.TenantConfig;
import com.inventory.management.service.TenantConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.inventory.management.security.JwtAuthenticationFilter;
import com.inventory.management.config.TestMongoConfig;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantConfigController.class)
@Import(TestMongoConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class TenantConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantConfigService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void bySubdomain_returnsOk() throws Exception {
        when(service.getConfigBySubDomain("acme")).thenReturn(new TenantConfig());
        mockMvc.perform(get("/api/tenant-config/by-subdomain/acme").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).getConfigBySubDomain("acme");
    }
}
