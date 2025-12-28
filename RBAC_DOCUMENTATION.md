# Role-Based Access Control (RBAC)

## Overview
Current implementation uses two roles with method-level guards via `@PreAuthorize`:

- `ADMIN`: Full administrative access to protected resources and audit queries
- `USER`: Standard authenticated access for read and non-destructive operations

Future roles (e.g., `PRODUCT_MANAGER`, `SUPPLIER_HANDLER`, `INVENTORY_MANAGER`) can be added later.

## Endpoint Access

### Auth (`/api/auth`)
- `POST /register` — Public
- `POST /login` — Public

### Products (`/api/products`)
- `GET /` — `ADMIN`, `USER`
- `GET /{id}` — `ADMIN`, `USER`
- `POST /` — `ADMIN`, `USER`
- `PUT /{id}` — `ADMIN`, `USER`
- `PATCH /{id}/qty` — `ADMIN`, `USER`
- `DELETE /{id}` — `ADMIN`

### Suppliers (`/api/suppliers`)
- `GET /` — `ADMIN`, `USER`
- `GET /{id}` — `ADMIN`, `USER`
- `PUT /{id}` — `ADMIN`, `USER`
- `DELETE /{id}` — `ADMIN`

### Invoices (`/api/invoices`)
- `GET /` — `ADMIN`, `USER`
- `GET /{invoiceNo}` — `ADMIN`, `USER`
- `GET /supplier/{supplierId}` — `ADMIN`, `USER`
- `GET /date-range` — `ADMIN`, `USER`
- `POST /` — `ADMIN`

### Product Batches (`/api/product-batches`)
- `GET /` — `ADMIN`, `USER`
- `GET /invoice/{invoiceNo}` — `ADMIN`, `USER`
- `GET /product/{productId}` — `ADMIN`, `USER`
- `GET /expiring?before=YYYY-MM-DD` — `ADMIN`, `USER`
- `POST /` — `ADMIN`

### Transactions (`/api/transactions`)
- `POST /` — `ADMIN`, `USER`
- `GET /` — `ADMIN`, `USER`
- `GET /{transactionId}` — `ADMIN`, `USER`
- `GET /outstanding` — `ADMIN`, `USER`
- `GET /date-range` — `ADMIN`, `USER`
- Items:
  - `GET /{transactionId}/items` — `ADMIN`, `USER`
  - `POST /{transactionId}/items` — `ADMIN`, `USER`
  - `DELETE /{transactionId}/items/{productId}` — `ADMIN`, `USER`

### Tenant Config (`/api/tenant-config`)
- `GET /by-subdomain/{subDomain}` — Intended public for frontend init; currently requires auth unless permitted in security config
- `GET /` — `ADMIN`, `USER`
- `PUT /` — `ADMIN`
- `POST /initialize` — `ADMIN`

### Audit Logs (`/api/audit-logs`)
- `GET /` — `ADMIN`
- `GET /user/{userId}` — `ADMIN`
- `GET /entity/{entityType}/{entityId}` — `ADMIN`, `USER`
- `GET /action/{actionType}` — `ADMIN`
- `GET /date-range` — `ADMIN`
- `GET /user/{userId}/date-range` — `ADMIN`
- `GET /failures` — `ADMIN`
- `GET /history/{entityType}/{entityId}` — `ADMIN`, `USER`
- `GET /report` — `ADMIN`

## Notes
- Authorities are granted via Spring Security with `ROLE_` prefix for `ADMIN` and `USER`.
- Additional roles can be mapped when introduced; update guards accordingly.

## Future Enhancements
- Add fine-grained roles (Product/Supplier/Inventory managers)
- Resource-level permissions
- Role management endpoints
- Audit filtering by role
