# API Testing Examples

This file contains example curl commands to test the Inventory Management API.

## Prerequisites
- The application should be running on http://localhost:8080
- MongoDB should be running on localhost:27017

## Authentication Endpoints

### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

Expected Response:
```json
{
  "message": "User registered successfully!"
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

Expected Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"]
}
```

**Save the token from the response for subsequent requests**

## Product Endpoints

**Note:** Replace `YOUR_JWT_TOKEN` with the actual token received from login.

### Create a product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "sku": "LAP-001",
    "price": 999.99,
    "quantity": 50,
    "category": "Electronics",
    "supplier": "Tech Supplies Inc"
  }'
```

### Get all products
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get product by ID
```bash
curl -X GET http://localhost:8080/api/products/{product_id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get product by SKU
```bash
curl -X GET http://localhost:8080/api/products/sku/LAP-001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get products by category
```bash
curl -X GET http://localhost:8080/api/products/category/Electronics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get products by supplier
```bash
curl -X GET http://localhost:8080/api/products/supplier/Tech%20Supplies%20Inc \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update a product
```bash
curl -X PUT http://localhost:8080/api/products/{product_id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Laptop Pro",
    "description": "Professional grade laptop",
    "sku": "LAP-001",
    "price": 1299.99,
    "quantity": 45,
    "category": "Electronics",
    "supplier": "Tech Supplies Inc"
  }'
```

### Update product stock
```bash
curl -X PATCH http://localhost:8080/api/products/{product_id}/stock \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "quantity": 100
  }'
```

### Delete a product (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/products/{product_id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Register Admin User

To create an admin user, include "ADMIN" in the roles array:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "roles": ["ADMIN", "USER"]
  }'
```

## Error Handling Examples

### Invalid credentials
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wrong_user",
    "password": "wrong_password"
  }'
```

Expected Response (401):
```json
{
  "timestamp": "2024-11-02T12:00:00",
  "message": "Invalid username or password",
  "status": 401
}
```

### Missing required field
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Laptop"
  }'
```

Expected Response (400):
```json
{
  "timestamp": "2024-11-02T12:00:00",
  "message": "Validation failed",
  "errors": {
    "sku": "SKU is required",
    "price": "Price is required",
    "quantity": "Quantity is required"
  },
  "status": 400
}
```

### Unauthorized access
```bash
curl -X GET http://localhost:8080/api/products
```

Expected Response (403):
```json
{
  "timestamp": "2024-11-02T12:00:00",
  "message": "Full authentication is required to access this resource",
  "status": 403
}

## Invoice Endpoints

### List all invoices
```bash
curl -X GET http://localhost:8080/api/invoices \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get invoice by number
```bash
curl -X GET http://localhost:8080/api/invoices/INV-1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Invoices by supplier
```bash
curl -X GET http://localhost:8080/api/invoices/supplier/SUP-1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Invoices by date range
```bash
curl -X GET "http://localhost:8080/api/invoices/date-range?start=2025-01-01&end=2025-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Create invoice (Admin)
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "invoiceNo": "INV-1002",
    "supplierId": "SUP-1",
    "date": "2025-01-15",
    "totalAmount": 1200.50
  }'
```

## Product Batch Endpoints

### List all batches
```bash
curl -X GET http://localhost:8080/api/product-batches \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Batches by invoice
```bash
curl -X GET http://localhost:8080/api/product-batches/invoice/INV-1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Batches by product
```bash
curl -X GET http://localhost:8080/api/product-batches/product/PROD-1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Expiring before date
```bash
curl -X GET "http://localhost:8080/api/product-batches/expiring?before=2025-02-01" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Create batch (Admin)
```bash
curl -X POST http://localhost:8080/api/product-batches \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "batchId": "BATCH-1",
    "productId": "PROD-1",
    "invoiceNo": "INV-1",
    "quantity": 50,
    "expiryDate": "2025-06-30"
  }'
```

## Transaction Endpoints

### Create transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "transactionId": "TXN-1",
    "timestamp": "2025-01-15T10:00:00Z",
    "totalAmount": 200.00,
    "paidAmount": 100.00,
    "status": "PARTIAL"
  }'
```

### List all transactions
```bash
curl -X GET http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Outstanding transactions
```bash
curl -X GET http://localhost:8080/api/transactions/outstanding \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Transactions by date range
```bash
curl -X GET "http://localhost:8080/api/transactions/date-range?start=2025-01-01T00:00:00Z&end=2025-01-31T23:59:59Z" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Transaction items
```bash
# Get items
curl -X GET http://localhost:8080/api/transactions/TXN-1/items \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Add item
curl -X POST http://localhost:8080/api/transactions/TXN-1/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "productId": "PROD-1",
    "quantity": 2,
    "unitPrice": 100.00
  }'

# Delete item
curl -X DELETE http://localhost:8080/api/transactions/TXN-1/items/PROD-1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Tenant Config Endpoints

### Get config by subdomain (frontend init, public)
```bash
curl -X GET http://localhost:8080/api/tenant-config/by-subdomain/acme \
  -H "Origin: acme.localhost:3000"
```

### Get current tenant config
```bash
curl -X GET http://localhost:8080/api/tenant-config \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Initialize default config (Admin)
```bash
curl -X POST http://localhost:8080/api/tenant-config/initialize \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update config (Admin)
```bash
curl -X PUT http://localhost:8080/api/tenant-config \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "brand": {"name": "Acme", "primaryColor": "#123456"},
    "uiTheme": {"mode": "dark"}
  }'
```

## Audit Log Endpoints (Admin unless noted)

```bash
# Tenant logs (paged)
curl -X GET "http://localhost:8080/api/audit-logs?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Logs by user
curl -X GET "http://localhost:8080/api/audit-logs/user/USER-1?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Logs by entity (authenticated)
curl -X GET "http://localhost:8080/api/audit-logs/entity/Product/PROD-1?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Logs by action type
curl -X GET "http://localhost:8080/api/audit-logs/action/CREATE?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Logs by date range
curl -X GET "http://localhost:8080/api/audit-logs/date-range?start=2025-01-01T00:00:00Z&end=2025-01-31T23:59:59Z&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# User logs by date range
curl -X GET "http://localhost:8080/api/audit-logs/user/USER-1/date-range?start=2025-01-01T00:00:00Z&end=2025-01-31T23:59:59Z&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Failed operations
curl -X GET "http://localhost:8080/api/audit-logs/failures?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Entity history (authenticated)
curl -X GET "http://localhost:8080/api/audit-logs/history/Product/PROD-1?start=2025-01-01T00:00:00Z&end=2025-01-31T23:59:59Z&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Audit report
curl -X GET "http://localhost:8080/api/audit-logs/report?userId=USER-1&actionType=UPDATE&start=2025-01-01T00:00:00Z&end=2025-01-31T23:59:59Z&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
```
