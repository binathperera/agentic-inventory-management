package com.inventory.management.controller;

import com.inventory.management.model.ProductBatch;
import com.inventory.management.service.ProductBatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.inventory.management.security.JwtAuthenticationFilter;
import com.inventory.management.config.TestMongoConfig;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductBatchController.class)
@Import(TestMongoConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductBatchService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = { "USER" })
    void byInvoice_returnsOk() throws Exception {
        when(service.getByInvoice("INV-1")).thenReturn(List.of(new ProductBatch()));
        mockMvc.perform(get("/api/product-batches/invoice/INV-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).getByInvoice("INV-1");
    }
}
