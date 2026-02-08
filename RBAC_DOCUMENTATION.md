# Role-Based Access Control (RBAC)

## Overview
The system implements a three-tier role-based access control model using JWT tokens and method-level guards via `@PreAuthorize`:

- `ADMIN`: Full administrative access to all resources, user management, and configuration
- `CASHIER`: Transaction and sales operations, can view and manage inventory
- `USER`: Read-only access to inventory and reports, **cannot modify any data**

## Role Capabilities

### ADMIN
- User management (create, modify, delete users)
- Full CRUD on products, suppliers, invoices, product batches
- Transaction management
- AI Chat access
- Tenant configuration
- Audit log access

### CASHIER
- View all products and suppliers (read-only on non-transaction endpoints)
- Manage transactions (create/update/view)
- AI Chat access
- View invoices and product batches
- View audit logs for specific entities

### USER (Default Role)
- View-only access to products, suppliers, invoices, product batches
- **Cannot create, modify, or delete any data**
- Can view audit history for specific entities they have access to
- **No transaction or AI Chat access**

## Endpoint Access Matrix

### Auth (`/api/auth`) - Public
- `POST /register` — Public (default role: USER)
- `POST /login` — Public

### Products (`/api/products`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET / | ✓ | ✓ | ✓ |
| GET /{id} | ✓ | ✓ | ✓ |
| POST / | ✗ | ✓ | ✓ |
| PUT /{id} | ✗ | ✓ | ✓ |
| PATCH /{id}/qty | ✗ | ✓ | ✓ |
| DELETE /{id} | ✗ | ✗ | ✓ |

### Suppliers (`/api/suppliers`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET / | ✓ | ✓ | ✓ |
| GET /{id} | ✓ | ✓ | ✓ |
| POST / | ✗ | ✓ | ✓ |
| PUT /{id} | ✗ | ✓ | ✓ |
| DELETE /{id} | ✗ | ✗ | ✓ |

### Invoices (`/api/invoices`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET / | ✓ | ✓ | ✓ |
| GET /{invoiceNo} | ✓ | ✓ | ✓ |
| GET /supplier/{supplierId} | ✓ | ✓ | ✓ |
| GET /date-range | ✓ | ✓ | ✓ |
| POST / | ✗ | ✗ | ✓ |
| DELETE / | ✗ | ✗ | ✓ |

### Product Batches (`/api/product-batches`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET / | ✓ | ✓ | ✓ |
| GET /invoice/{invoiceNo} | ✓ | ✓ | ✓ |
| GET /product/{productId} | ✓ | ✓ | ✓ |
| GET /expiring?before=YYYY-MM-DD | ✓ | ✓ | ✓ |
| POST / | ✗ | ✗ | ✓ |
| DELETE / | ✗ | ✗ | ✓ |

### Transactions (`/api/transactions`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| POST / | ✗ | ✓ | ✓ |
| GET / | ✗ | ✓ | ✓ |
| GET /{transactionId} | ✗ | ✓ | ✓ |
| GET /outstanding | ✗ | ✓ | ✓ |
| GET /date-range | ✗ | ✓ | ✓ |
| Items (POST/DELETE) | ✗ | ✓ | ✓ |

### AI Chat (`/api/chat`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| All endpoints | ✗ | ✓ | ✓ |

### Tenant Config (`/api/tenant-config`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET /by-subdomain/{subDomain} | ✓ | ✓ | ✓ |
| GET / | ✓ | ✓ | ✓ |
| PUT / | ✗ | ✗ | ✓ |
| POST /initialize | ✗ | ✗ | ✓ |

### User Management (`/api/users`) - ADMIN ONLY
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| All endpoints | ✗ | ✗ | ✓ |

### Audit Logs (`/api/audit-logs`)
| Endpoint | USER | CASHIER | ADMIN |
|----------|------|---------|-------|
| GET / | ✗ | ✗ | ✓ |
| GET /user/{userId} | ✗ | ✗ | ✓ |
| GET /entity/{entityType}/{entityId} | ✓ | ✓ | ✓ |
| GET /action/{actionType} | ✗ | ✗ | ✓ |
| GET /date-range | ✗ | ✗ | ✓ |
| GET /user/{userId}/date-range | ✗ | ✗ | ✓ |
| GET /failures | ✗ | ✗ | ✓ |
| GET /history/{entityType}/{entityId} | ✓ | ✓ | ✓ |
| GET /report | ✗ | ✗ | ✓ |

## Implementation Notes

- Authorities are granted via Spring Security with `ROLE_` prefix (e.g., `ROLE_ADMIN`, `ROLE_CASHIER`, `ROLE_USER`)
- Spring Security configuration uses HTTP method matching for granular control
- Role validation is enforced both at endpoint level and method level
- Default role for new user registrations is `USER` (read-only access)
- Admins can create users with any valid role (ADMIN, CASHIER, USER)
- Role comparison uses proper equals() and hashCode() implementation for reliable role management

## Role Management Endpoints (ADMIN ONLY)

- `POST /api/users/{id}/roles/{roleName}` — Add a role to a user (validate against ADMIN, CASHIER, USER)
- `DELETE /api/users/{id}/roles/{roleName}` — Remove a role from a user

## Security Best Practices

1. **Default to Restrictive**: New registrations default to USER role (read-only)
2. **Role Validation**: Invalid role names are rejected at endpoint level
3. **Tenant Isolation**: All operations are filtered by tenant context
4. **Audit Trail**: Admin operations are tracked in audit logs
5. **Stateless Auth**: JWT tokens include tenant ID and roles for stateless authentication

## Future Enhancements

- Fine-grained permissions (e.g., per-product or per-supplier access)
- Dynamic role creation and permission assignment
- Time-based role elevation
- Activity-based role adjustments
