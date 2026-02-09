# Agentic Inventory Management System

REST API for multi-tenant inventory management built with Spring Boot, MongoDB, and JWT authentication. Includes tenant-aware products, invoices, transactions, product batches, and comprehensive audit logging.

## Features

- **Multi-Tenancy by Subdomain**: Resolve tenant from request Origin subdomain and JWT `tenantId` claim
- **JWT Authentication**: Secure stateless auth with tenant-aware user lookup
- **Audit Logging (AOP)**: Automatic logging for write operations; rich query endpoints
- **Inventory Domains**: Products, Invoices, Transactions, and Product Batches
- **MongoDB Integration**: NoSQL storage with tenant-scoped indexes
- **Role-Based Access**: `ADMIN` and `USER` with endpoint guards
- **Environment-Driven Config**: Mongo URI/DB, allowed client URLs, JWT settings
- **Dockerized**: Multi-stage Dockerfile for production-ready container builds

## Technologies Used

- Spring Boot 3.1.5, Spring Security, Spring Data MongoDB
- JWT (jjwt), AOP, Lombok
- Maven, Java 17

## Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB (local or cloud)

## Configuration (Environment Variables)

The app reads env vars with sensible defaults. Key properties in [src/main/resources/application.properties](src/main/resources/application.properties):

```properties
# Server
server.port=8080

# Allowed client origins (comma-separated patterns)
client.allowed.urls=${ALLOWED_CLIENT_URLS:http://*.localhost:3000,http://43.204.141.135}

# MongoDB
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017}
spring.data.mongodb.database=${SPRING_DATA_MONGODB_DATABASE:inventory_db}

# JWT
jwt.secret=${JWT_SECRET:change-me}
jwt.expiration=${JWT_EXPIRATION_MS:86400000}
```

Set these with your environment or pass `-D` system properties.

## Installation & Run (Local)

1. Clone the repository:
```bash
git clone https://github.com/binathperera/agentic-inventory-management.git
cd agentic-inventory-management
```

2. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

App runs at http://localhost:8080

## Docker

Build and run with Docker (no Compose required):

```bash
docker build -t agentic-inventory .
docker run --rm -p 8080:8080 ^
  -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017" ^
  -e SPRING_DATA_MONGODB_DATABASE="inventory_db" ^
  -e ALLOWED_CLIENT_URLS="http://localhost:3000,http://localhost:5173" ^
  -e JWT_SECRET="your-production-secret" ^
  agentic-inventory
```

On Linux/macOS, replace `^` with `\` and `host.docker.internal` with your host IP if needed.

## Multi-Tenant Behavior

- Tenant is resolved from the request `Origin` header subdomain (e.g., `tenant1.localhost:3000`) when no JWT is present.
- When a JWT is provided, the `tenantId` claim is used.
- Ensure frontend requests include the `Origin` header (browsers add it automatically). For `curl`, you can add `-H "Origin: tenant1.localhost:3000"`.

## API Overview

### Authentication
- `POST /api/auth/register` — Public
- `POST /api/auth/login` — Public

### Products
- `GET /api/products` — Authenticated
- `GET /api/products/{id}` — Authenticated
- `POST /api/products` — Authenticated
- `PUT /api/products/{id}` — Authenticated
- `PATCH /api/products/{id}/qty` — Authenticated
- `DELETE /api/products/{id}` — `ADMIN`

### Suppliers
- `GET /api/suppliers` — Authenticated
- `GET /api/suppliers/{id}` — Authenticated
- `PUT /api/suppliers/{id}` — Authenticated
- `DELETE /api/suppliers/{id}` — `ADMIN`

### Invoices
- `GET /api/invoices` — Authenticated
- `GET /api/invoices/{invoiceNo}` — Authenticated
- `GET /api/invoices/supplier/{supplierId}` — Authenticated
- `GET /api/invoices/date-range?start=YYYY-MM-DD&end=YYYY-MM-DD` — Authenticated
- `POST /api/invoices` — `ADMIN`

### Product Batches
- `GET /api/product-batches` — Authenticated
- `GET /api/product-batches/invoice/{invoiceNo}` — Authenticated
- `GET /api/product-batches/product/{productId}` — Authenticated
- `GET /api/product-batches/expiring?before=YYYY-MM-DD` — Authenticated
- `POST /api/product-batches` — `ADMIN`

### Transactions
- `POST /api/transactions` — Authenticated
- `GET /api/transactions` — Authenticated
- `GET /api/transactions/{transactionId}` — Authenticated
- `GET /api/transactions/outstanding` — Authenticated
- `GET /api/transactions/date-range?start=ISO&end=ISO` — Authenticated
- Items:
  - `GET /api/transactions/{transactionId}/items` — Authenticated
  - `POST /api/transactions/{transactionId}/items` — Authenticated
  - `DELETE /api/transactions/{transactionId}/items/{productId}` — Authenticated

### Tenant Config
- `GET /api/tenant-config/by-subdomain/{subDomain}` — Public (used by frontend init; resolves tenant by subdomain)
- `GET /api/tenant-config` — Authenticated
- `PUT /api/tenant-config` — `ADMIN`
- `POST /api/tenant-config/initialize` — `ADMIN`

### Audit Logs
- `GET /api/audit-logs` — `ADMIN`
- `GET /api/audit-logs/user/{userId}` — `ADMIN`
- `GET /api/audit-logs/entity/{entityType}/{entityId}` — Authenticated
- `GET /api/audit-logs/action/{actionType}` — `ADMIN`
- `GET /api/audit-logs/date-range?start=ISO&end=ISO` — `ADMIN`
- `GET /api/audit-logs/user/{userId}/date-range?start=ISO&end=ISO` — `ADMIN`
- `GET /api/audit-logs/failures` — `ADMIN`
- `GET /api/audit-logs/history/{entityType}/{entityId}?start=ISO&end=ISO` — Authenticated
- `GET /api/audit-logs/report?userId=...&actionType=...&start=ISO&end=ISO` — `ADMIN`

## Security

- BCrypt password hashing
- JWT expiration configurable via `jwt.expiration`
- Guards via `@PreAuthorize` (`ADMIN`, `USER`)
- CORS origins configured via `client.allowed.urls`

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/inventory/management/
│   │   ├── config/          # Security, AOP, and application config
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handling
│   │   ├── model/           # Entity models
│   │   ├── repository/      # Mongo repositories
│   │   ├── security/        # JWT utils and filter
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/inventory/management/  # Unit & MVC tests
```

## License

Apache License — see [LICENSE](LICENSE)
