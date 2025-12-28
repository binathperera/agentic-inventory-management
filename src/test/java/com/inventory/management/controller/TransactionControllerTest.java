package com.inventory.management.controller;

import com.inventory.management.model.Transaction;
import com.inventory.management.model.TransactionItem;
import com.inventory.management.service.TransactionService;
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

@WebMvcTest(TransactionController.class)
@Import(TestMongoConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = { "USER" })
    void listAll_returnsOk() throws Exception {
        when(service.getAll()).thenReturn(List.of(new Transaction()));
        mockMvc.perform(get("/api/transactions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).getAll();
    }

    @Test
    @WithMockUser(roles = { "USER" })
    void getItems_returnsOk() throws Exception {
        when(service.getItems("TXN-1")).thenReturn(List.of(new TransactionItem()));
        mockMvc.perform(get("/api/transactions/TXN-1/items").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).getItems("TXN-1");
    }
}
