package com.inventory.management.controller;

import com.inventory.management.service.AiChatService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.bson.Document;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Useful for your React frontend development
public class AiChatController {

    private final AiChatService aiService;

    public AiChatController(AiChatService aiService) {
        this.aiService = aiService;
    }

    /**
     * Endpoint for Natural Language Inventory Queries
     * Example: "Show me items with quantity less than 10 in the Colombo warehouse"
     */
    @GetMapping("/query")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Document> query(@RequestParam String prompt) {
        return aiService.query(prompt);
    }
}
