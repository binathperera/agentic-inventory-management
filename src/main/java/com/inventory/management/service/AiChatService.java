package com.inventory.management.service;

import org.springframework.ai.chat.client.ChatClient;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.mongodb.client.MongoCollection;
import com.inventory.management.config.TenantContext;

@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final MongoTemplate mongoTemplate;

    private final String schema = """
            Collections and fields (use exact field names):
            - product: {tenant_id: string, _id: string, name: string, latest_batch_no: string, remaining_quantity: int, latest_unit_price: double, created_at: date, updated_at: date, schema_version: int}
            - supplier: {tenant_id: string, _id: string, name: string, email: string, address: string, contact: string, created_at: date, updated_at: date, schema_version: int}
            - product_batch: {tenant_id: string, _id: string, product_id: string, invoice_no: string, batch_no: string, qty: int, unit_cost: double, unit_price: double, exp: date, created_at: date, updated_at: date, schema_version: int}
            - invoice: {tenant_id: string, _id: string, invoice_no: string, supplier_id: string, date: date, created_at: date, updated_at: date, schema_version: int}
            - transaction: {tenant_id: string, _id: string, transaction_id: string, payment_method: string, gross_amount: double, discount_amount: double, net_amount: double, paid_amount: double, balance_amount: double, created_at: date, updated_at: date, schema_version: int}
            - transaction_item: {tenant_id: string, _id: string, transaction_id: string, product_id: string, qty: int, unit_price: double, created_at: date, updated_at: date, schema_version: int}
            - audit_log: {tenant_id: string, _id: string, user_id: string, username: string, entity_type: string, entity_id: string, action_type: string, description: string, old_values: object, new_values: object, status: string, error_message: string, ip_address: string, user_agent: string, request_path: string, request_method: string, duration_ms: long, timestamp: date, schema_version: int}
            - tenant: { _id: string, name: string, sub_domain: string, created_at: date, updated_at: date, schema_version: int}
            - tenant_config: { _id: string, tenant_id: string, brand: object, ui_theme: object, localization: object, features: object, created_at: date, updated_at: date, schema_version: int}
            - user: {tenant_id: string, _id: string, username: string, email: string, password: string, roles: array, enabled: bool, created_at: date, updated_at: date, schema_version: int}

            Allowed response formats (choose one):
            1) Find: {"collection":"product", "filter":{...}, "projection":{...}, "sort":{...}, "limit":10, "skip":0}
            2) Aggregate (for multi-collection joins/lookups, group, etc.): {"collection":"product", "pipeline":[ {"$match":{...}}, {"$lookup":{...}}, {"$group":{...}}, {"$sort":{...}}, {"$limit":...} ]}
            Always include the target collection name. Only return JSON, no explanation.
            """;

    public AiChatService(ChatClient.Builder builder, MongoTemplate mongoTemplate) {
        this.chatClient = builder
                .defaultSystem("You are an expert in MongoDB. Given the following collections: " +
                        schema +
                        "Convert the user's natural language request into a valid MongoDB JSON query object. " +
                        "Return ONLY the JSON query.")
                .build();
        this.mongoTemplate = mongoTemplate;
    }

    public List<Document> query(String userMessage) {
        // 1. Generate the MQL JSON string from the LLM
        String mqlQuery = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        // 2. Parse the JSON into a document and extract query parts
        Document requestDoc = parseRequestDocument(mqlQuery);

        String collection = requestDoc.getString("collection");
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("Query must include 'collection'");
        }

        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant context is missing");
        }

        // If pipeline is present, run aggregation; otherwise run find
        List<Document> pipeline = requestDoc.getList("pipeline", Document.class);
        if (pipeline != null && !pipeline.isEmpty()) {
            List<Document> safePipeline = new ArrayList<>();
            for (Object stageObj : pipeline) {
                if (stageObj instanceof Document) {
                    safePipeline.add((Document) stageObj);
                } else if (stageObj instanceof java.util.Map) {
                    // Normalize arbitrary map into a BSON Document by stringifying keys
                    Document stageDoc = new Document();
                    java.util.Map<?, ?> raw = (java.util.Map<?, ?>) stageObj;
                    for (java.util.Map.Entry<?, ?> entry : raw.entrySet()) {
                        stageDoc.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                    safePipeline.add(stageDoc);
                } else if (stageObj instanceof String) {
                    safePipeline.add(Document.parse((String) stageObj));
                } else {
                    throw new IllegalArgumentException("Invalid aggregation stage type: " + stageObj.getClass());
                }
            }

            // Enforce tenant isolation at the first match stage or prepend one
            if (safePipeline.isEmpty() || !safePipeline.get(0).containsKey("$match")) {
                safePipeline.add(0, new Document("$match", new Document("tenant_id", tenantId)));
            } else {
                Document matchDoc = safePipeline.get(0).get("$match", Document.class);
                if (matchDoc == null) {
                    matchDoc = new Document();
                }
                matchDoc.put("tenant_id", tenantId);
                safePipeline.set(0, new Document("$match", matchDoc));
            }

            MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collection);
            return mongoCollection.aggregate(safePipeline).into(new ArrayList<>());
        }

        Document filter = requestDoc.get("filter", Document.class);
        if (filter == null) {
            // If no explicit filter key, use remaining fields as filter
            filter = new Document(requestDoc);
            filter.remove("collection");
            filter.remove("projection");
            filter.remove("sort");
            filter.remove("limit");
            filter.remove("skip");
            filter.remove("pipeline");
        }
        if (filter == null) {
            filter = new Document();
        }

        // Enforce tenant isolation regardless of the LLM output
        filter.put("tenant_id", tenantId);

        BasicQuery query = new BasicQuery(filter);

        Document projection = requestDoc.get("projection", Document.class);
        if (projection != null) {
            query.setFieldsObject(projection);
        }

        Document sort = requestDoc.get("sort", Document.class);
        if (sort != null) {
            query.setSortObject(sort);
        }

        Integer limit = requestDoc.getInteger("limit");
        if (limit != null) {
            query.limit(limit);
        }

        Integer skip = requestDoc.getInteger("skip");
        if (skip != null) {
            query.skip(skip);
        }

        // 3. Execute against the requested collection
        return mongoTemplate.find(query, Document.class, collection);
    }

    private Document parseRequestDocument(String mqlQuery) {
        if (mqlQuery == null || mqlQuery.isBlank()) {
            throw new IllegalArgumentException("LLM returned empty JSON query");
        }

        String cleaned = mqlQuery.trim();

        // Strip Markdown code fences like ```json ... ``` if present
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            if (firstNewline > 0) {
                cleaned = cleaned.substring(firstNewline + 1);
            } else {
                cleaned = cleaned.substring(3);
            }

            int closingFence = cleaned.lastIndexOf("```");
            if (closingFence >= 0) {
                cleaned = cleaned.substring(0, closingFence);
            }
            cleaned = cleaned.trim();
        }

        try {
            System.out.println("LLM returned MQL: " + cleaned);
            return Document.parse(cleaned);
        } catch (Exception primary) {
            // Fallback: try the first balanced JSON object portion
            int firstBrace = cleaned.indexOf('{');
            int lastBrace = cleaned.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                String slice = cleaned.substring(firstBrace, lastBrace + 1);
                try {
                    return Document.parse(slice);
                } catch (Exception ignored) {
                    // Continue to throw the original error
                }
            }
            throw new IllegalArgumentException("LLM returned invalid JSON query", primary);
        }
    }
}