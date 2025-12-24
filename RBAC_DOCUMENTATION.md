# Role-Based Access Control (RBAC) System

## Overview
The inventory management system implements a comprehensive role-based access control system with 5 distinct user roles, each with different levels of access to features and data.

## User Roles

### 1. **ADMIN** (Level 1 - Highest Privilege)
- **Description**: System Administrator
- **Permissions**:
  - ✅ Full system access to all resources
  - ✅ Can manage all products (create, read, update, delete)
  - ✅ Can manage all suppliers (create, read, update, delete)
  - ✅ Can update inventory stock levels
  - ✅ Can manage users and assign roles
  - ✅ Can access system settings and configurations

**Use Case**: System administrators, IT personnel

---

### 2. **PRODUCT_MANAGER** (Level 2)
- **Description**: Product Manager
- **Permissions**:
  - ✅ Can view all products and suppliers
  - ✅ Can create new products
  - ✅ Can update existing products
  - ❌ Cannot delete products
  - ✅ Can update inventory stock levels
  - ❌ Cannot manage suppliers (view only)
  - ❌ Cannot manage users

**Use Case**: Product managers, inventory planners

---

### 3. **SUPPLIER_HANDLER** (Level 3)
- **Description**: Supplier Handler
- **Permissions**:
  - ✅ Can view all products and suppliers
  - ✅ Can create new suppliers
  - ✅ Can update supplier information
  - ❌ Cannot delete suppliers
  - ❌ Cannot create or edit products
  - ❌ Cannot update stock levels
  - ❌ Cannot delete any resources

**Use Case**: Supplier relationship managers, procurement specialists

---

### 4. **INVENTORY_MANAGER** (Level 4)
- **Description**: Inventory Manager
- **Permissions**:
  - ✅ Can view all products and suppliers
  - ✅ Can update inventory stock levels
  - ✅ Can create/update products
  - ❌ Cannot delete products
  - ❌ Cannot manage suppliers
  - ❌ Cannot delete any resources

**Use Case**: Warehouse managers, stock controllers

---

### 5. **USER** (Level 5 - Lowest Privilege)
- **Description**: Standard User
- **Permissions**:
  - ✅ Can view all products and suppliers (read-only)
  - ✅ Can create new products
  - ❌ Cannot update products
  - ❌ Cannot delete products
  - ❌ Cannot update stock levels
  - ❌ Cannot manage suppliers
  - ❌ Cannot delete any resources

**Use Case**: Regular staff, data entry operators

---

## API Endpoints by Role

### Products API (`/api/products`)

| Endpoint | Method | GET | POST | PUT | DELETE | PATCH (Stock) |
|----------|--------|-----|------|-----|--------|---------------|
| `/api/products` | - | All* | ADMIN, PRODUCT_MANAGER, USER | ADMIN, PRODUCT_MANAGER | ADMIN | ADMIN, PRODUCT_MANAGER, INVENTORY_MANAGER |
| `/api/products/{id}` | - | All* | - | ADMIN, PRODUCT_MANAGER | ADMIN | - |
| `/api/products/{id}/stock` | PATCH | - | - | - | - | ADMIN, PRODUCT_MANAGER, INVENTORY_MANAGER |

*All authenticated users can view products

### Suppliers API (`/api/suppliers`)

| Endpoint | Method | GET | POST | PUT | DELETE |
|----------|--------|-----|------|-----|--------|
| `/api/suppliers` | - | ADMIN, PRODUCT_MANAGER, SUPPLIER_HANDLER, INVENTORY_MANAGER | ADMIN, SUPPLIER_HANDLER | ADMIN, SUPPLIER_HANDLER | ADMIN |
| `/api/suppliers/{id}` | - | ADMIN, PRODUCT_MANAGER, SUPPLIER_HANDLER, INVENTORY_MANAGER | - | ADMIN, SUPPLIER_HANDLER | ADMIN |

### Auth API (`/api/auth`)

| Endpoint | Method | Access |
|----------|--------|--------|
| `/api/auth/login` | POST | Public |
| `/api/auth/register` | POST | Public |
| `/api/auth/roles` | GET | Public (returns available roles with descriptions) |

---

## How to Use Different Roles

### Registration with a Specific Role

When registering a user, include the desired role(s) in the request:

```json
{
  "username": "supplier_manager",
  "email": "supplier@example.com",
  "password": "securePassword123",
  "roles": ["SUPPLIER_HANDLER"]
}
```

### Available Roles Endpoint

To get all available roles with descriptions:

```bash
GET /api/auth/roles
```

Response:
```json
{
  "roles": {
    "ADMIN": {
      "description": "System Administrator",
      "level": "1"
    },
    "PRODUCT_MANAGER": {
      "description": "Product Manager",
      "level": "2"
    },
    "SUPPLIER_HANDLER": {
      "description": "Supplier Handler",
      "level": "3"
    },
    "INVENTORY_MANAGER": {
      "description": "Inventory Manager",
      "level": "4"
    },
    "USER": {
      "description": "Standard User",
      "level": "5"
    }
  }
}
```

---

## Implementation Details

### UserRole Enum
Located in: `com.inventory.management.model.UserRole`

The `UserRole` enum defines all available roles with:
- Role name
- Description
- Privilege level
- `hasHigherOrEqualPrivilege()` method for privilege comparison

### Spring Security Integration

The system uses Spring Security's `@PreAuthorize` annotation for method-level security:

```java
@PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
public ResponseEntity<Product> updateProduct(String id, ProductRequest request) {
    // Only ADMIN or PRODUCT_MANAGER can execute this
}
```

### User Details Service
Located in: `com.inventory.management.service.UserDetailsServiceImpl`

Converts stored user roles into Spring Security `GrantedAuthority` objects with "ROLE_" prefix.

---

## Access Control Matrix

| Feature | ADMIN | PRODUCT_MANAGER | SUPPLIER_HANDLER | INVENTORY_MANAGER | USER |
|---------|:-----:|:---------------:|:----------------:|:-----------------:|:----:|
| View Products | ✅ | ✅ | ✅ | ✅ | ✅ |
| Create Products | ✅ | ✅ | ❌ | ✅ | ✅ |
| Update Products | ✅ | ✅ | ❌ | ✅ | ❌ |
| Delete Products | ✅ | ❌ | ❌ | ❌ | ❌ |
| View Suppliers | ✅ | ✅ | ✅ | ✅ | ❌ |
| Create Suppliers | ✅ | ❌ | ✅ | ❌ | ❌ |
| Update Suppliers | ✅ | ❌ | ✅ | ❌ | ❌ |
| Delete Suppliers | ✅ | ❌ | ❌ | ❌ | ❌ |
| Update Stock | ✅ | ✅ | ❌ | ✅ | ❌ |

---

## Testing Roles

### 1. Register a SUPPLIER_HANDLER user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "supplier_handler_1",
    "email": "handler@supplier.com",
    "password": "password123",
    "roles": ["SUPPLIER_HANDLER"]
  }'
```

### 2. Login as SUPPLIER_HANDLER
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "supplier_handler_1",
    "password": "password123"
  }'
```

### 3. Use token to access supplier endpoints
```bash
curl -X GET http://localhost:8080/api/suppliers \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 4. Try to access product update (should be denied)
```bash
curl -X PUT http://localhost:8080/api/products/123 \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{...product data...}'
```

This request will return **403 Forbidden** because SUPPLIER_HANDLER cannot update products.

---

## Future Enhancements

1. **Multi-role Support**: Users can have multiple roles simultaneously
2. **Custom Permissions**: Define custom permissions per role
3. **Role Management API**: Endpoints to create and manage roles dynamically
4. **Audit Logging**: Log all actions by role for compliance
5. **Resource-Level Permissions**: Restrict access to specific resources based on role
6. **Time-based Access**: Enable/disable roles for specific time periods
