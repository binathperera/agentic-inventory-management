package com.inventory.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class McpService {

    private static final String PROTOCOL_VERSION = "2025-03-26";
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl;

    public McpService(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${app.mcp.api-base-url:http://127.0.0.1:8080}") String apiBaseUrl) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.apiBaseUrl = apiBaseUrl.replaceAll("/$", "");
    }

    public JsonNode handle(JsonNode request, String authorizationHeader) {
        JsonNode id = request.get("id");
        String method = request.path("method").asText();

        return switch (method) {
            case "initialize" -> result(id, initializeResult());
            case "notifications/initialized" -> null;
            case "tools/list" -> result(id, toolsResult());
            case "tools/call" -> result(id, callTool(request.path("params"), authorizationHeader));
            default -> error(id, -32601, "Method not found: " + method);
        };
    }

    private ObjectNode initializeResult() {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", PROTOCOL_VERSION);
        result.putObject("capabilities").putObject("tools");
        result.putObject("serverInfo").put("name", "inventory-management").put("version", "1.0.0");
        return result;
    }

    private ObjectNode toolsResult() {
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tools = result.putArray("tools");
        addInvoiceTool(tools, "create_invoice", "Create an invoice. Requires ADMIN or MANAGER role.", "POST",
                "/api/invoices", false);
        addInvoiceTool(tools, "update_invoice", "Update an invoice supplier or date. Requires ADMIN or MANAGER role.",
                "PUT", "/api/invoices/{invoiceNo}", true);
        addInvoiceTool(tools, "delete_invoice", "Delete an invoice. Requires ADMIN role.", "DELETE",
                "/api/invoices/{invoiceNo}", true);
        return result;
    }

    private void addInvoiceTool(ArrayNode tools, String name, String description, String method, String path,
            boolean requiresInvoiceNo) {
        ObjectNode tool = tools.addObject().put("name", name).put("description", description);
        ObjectNode properties = tool.putObject("inputSchema").putObject("properties");
        if (requiresInvoiceNo) {
            properties.putObject("invoiceNo").put("type", "string");
        }
        if (!"DELETE".equals(method)) {
            properties.putObject("invoice").put("type", "object").put("description",
                    "Invoice JSON. Include supplierId and date.");
        }
        tool.with("inputSchema").putArray("required").add(requiresInvoiceNo ? "invoiceNo" : "invoice");
    }

    private ObjectNode callTool(JsonNode params, String authorizationHeader) {
        String name = params.path("name").asText();
        if (!name.matches("create_invoice|update_invoice|delete_invoice")) {
            return toolError("Unknown tool: " + name);
        }

        JsonNode arguments = params.path("arguments");
        String method = name.equals("create_invoice") ? "POST" : name.equals("update_invoice") ? "PUT" : "DELETE";
        String path = "/api/invoices";
        if (!"create_invoice".equals(name)) {
            String invoiceNo = arguments.path("invoiceNo").asText("");
            if (invoiceNo.isBlank() || invoiceNo.contains("/")) {
                return toolError("invoiceNo is required and cannot contain '/'");
            }
            path += "/" + java.net.URLEncoder.encode(invoiceNo, java.nio.charset.StandardCharsets.UTF_8);
        }

        try {
            RestClient.RequestBodySpec request = restClient.method(org.springframework.http.HttpMethod.valueOf(method))
                    .uri(apiBaseUrl + path);
            if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                request.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
            if (arguments.has("invoice") && !arguments.get("invoice").isNull()) {
                request.contentType(MediaType.APPLICATION_JSON).body(arguments.get("invoice"));
            }
            String response = request.retrieve().body(String.class);
            return toolResult(response == null ? "" : response);
        } catch (Exception exception) {
            return toolError(exception.getMessage() == null ? "Request failed" : exception.getMessage());
        }
    }

    private ObjectNode toolResult(String text) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("isError", false).putArray("content").addObject().put("type", "text").put("text", text);
        return result;
    }

    private ObjectNode toolError(String message) {
        ObjectNode result = toolResult(message);
        result.put("isError", true);
        return result;
    }

    private ObjectNode result(JsonNode id, JsonNode result) {
        ObjectNode response = objectMapper.createObjectNode();
        response.set("jsonrpc", objectMapper.getNodeFactory().textNode("2.0"));
        response.set("id", id);
        response.set("result", result);
        return response;
    }

    private ObjectNode error(JsonNode id, int code, String message) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);
        response.putObject("error").put("code", code).put("message", message);
        return response;
    }
}