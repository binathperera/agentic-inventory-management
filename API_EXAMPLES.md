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
```
