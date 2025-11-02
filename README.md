# Agentic Inventory Management System

REST API for agentic inventory management system built with Spring Boot, MongoDB, and JWT authentication.

## Features

- **JWT Authentication**: Secure user authentication and authorization
- **MongoDB Integration**: NoSQL database for flexible data storage
- **User Management**: User registration and login with role-based access control
- **Product Management**: Full CRUD operations for inventory products
- **RESTful API**: Clean REST endpoints for all operations
- **Validation**: Input validation for all API requests
- **Error Handling**: Global exception handling with meaningful error messages

## Technologies Used

- **Spring Boot 3.1.5**: Backend framework
- **Spring Security**: Security and authentication
- **Spring Data MongoDB**: MongoDB integration
- **JWT (JSON Web Tokens)**: Token-based authentication
- **Maven**: Build and dependency management
- **Lombok**: Reduce boilerplate code
- **Java 17**: Programming language

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MongoDB 4.0 or higher

## Installation

1. Clone the repository:
```bash
git clone https://github.com/binathperera/agentic-inventory-management.git
cd agentic-inventory-management
```

2. Start MongoDB using Docker Compose:
```bash
docker-compose up -d
```

Alternatively, install and run MongoDB locally or use a cloud MongoDB instance.

3. Configure MongoDB connection in `src/main/resources/application.properties` if needed:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/inventory_db
```

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Quick Start

See [API_EXAMPLES.md](API_EXAMPLES.md) for detailed curl command examples to test all endpoints.

## API Endpoints

### Authentication

#### Register a new user
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["USER"]
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"]
}
```

### Products (Requires Authentication)

Add the JWT token to the Authorization header for all product endpoints:
```
Authorization: Bearer <your-jwt-token>
```

#### Get all products
```http
GET /api/products
```

#### Get product by ID
```http
GET /api/products/{id}
```

#### Get product by SKU
```http
GET /api/products/sku/{sku}
```

#### Get products by category
```http
GET /api/products/category/{category}
```

#### Get products by supplier
```http
GET /api/products/supplier/{supplier}
```

#### Create a new product
```http
POST /api/products
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "sku": "LAP-001",
  "price": 999.99,
  "quantity": 50,
  "category": "Electronics",
  "supplier": "Tech Supplies Inc"
}
```

#### Update a product
```http
PUT /api/products/{id}
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "name": "Laptop Pro",
  "description": "Professional grade laptop",
  "sku": "LAP-001",
  "price": 1299.99,
  "quantity": 45,
  "category": "Electronics",
  "supplier": "Tech Supplies Inc"
}
```

#### Update product stock
```http
PATCH /api/products/{id}/stock
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "quantity": 100
}
```

#### Delete a product (Admin only)
```http
DELETE /api/products/{id}
Authorization: Bearer <your-jwt-token>
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- Role-based access control (USER, ADMIN)
- All product endpoints require authentication
- Delete operations require ADMIN role

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server port
server.port=8080

# MongoDB connection
spring.data.mongodb.uri=mongodb://localhost:27017/inventory_db

# JWT settings
jwt.secret=<your-secret-key>
jwt.expiration=86400000  # 24 hours in milliseconds
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/inventory/management/
│   │   ├── config/          # Security and application configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handling
│   │   ├── model/           # Entity models
│   │   ├── repository/      # MongoDB repositories
│   │   ├── security/        # JWT utilities and filters
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/inventory/management/  # Unit tests
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
