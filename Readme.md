# Stockee. Backend

A RESTful API for inventory management, waste tracking, order recommendations, and analytics.  
Built with Spring Boot, JWT-based authentication, role-based access control, and JPA persistence.

---

## 1. Installation & run

Clone and run:

```bash
git clone <your-repo-url>
cd stockee_backend
./mvnw clean package
./mvnw spring-boot:run
```

Default app URL: `http://localhost:8080`  
Default API base path is controlled by `api-endpoint` (see configuration); examples below assume `api-endpoint=/api`.

### Run tests
```bash
./mvnw test
```

---

## 2. Configuration

Add to `src/main/resources/application.properties`:

```properties
api-endpoint=/api
jwt.key=your-very-secret-key-which-should-be-long-and-random

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**CORS** — default: `http://localhost:5173`

---

## 3. Architecture & packages

```
dev.paula.stockee_backend
├── auth
│   ├── AuthController.java
│   ├── AuthService.java
│   └── JwtUtils.java
├── config
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   └── JwtAuthenticationFilter.java
├── analytics
│   ├── AnalyticsController.java
│   └── AnalyticsService.java
├── role
│   ├── RoleEntity.java
│   └── RoleRepository.java
├── user
│   ├── UserEntity.java
│   ├── UserRepository.java
│   └── UserService.java
└── StockeeBackendApplication.java
```

---

## 4. Diagram Class
```mermaid
%%{init: {'theme': 'default'}}%%
classDiagram
    class UserEntity {
        + Long id
        + String username
        + String email
        + String password
        + Set<RoleEntity> roles
    }

    class RoleEntity {
        + Long id
        + String name
        + Set<UserEntity> users
    }

    UserEntity "0..*" -- "0..*" RoleEntity : roles

    class StockEntity {
        + Long id
        + String name
        + double currentStock
        + double minimumStock
        + String unit
        + LocalDateTime lastUpdate
    }

    class WasteEntity {
        + Long id
        + Double quantity
        + String unit
        + String reason
        + String details
        + LocalDateTime timestamp
        + StockEntity ingredient
    }

    WasteEntity "0..*" --> "1" StockEntity : ingredient

    class OrderEntity {
        + Long id
        + LocalDateTime orderDate
        + Integer itemCount
        + String notes
        + List<OrderItemEntity> items
    }

    class OrderItemEntity {
        + Long id
        + BigDecimal quantity
        + String unit
        + StockEntity stock
        + OrderEntity order
    }

    OrderEntity "1" -- "0..*" OrderItemEntity : items
    OrderItemEntity --> StockEntity : stock
    OrderItemEntity --> OrderEntity : order

```

---

## 5. Authentication Flow 

```mermaid
%%{init: {'theme': 'default'}}%%
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant JwtUtils
    participant DB

    Client->>AuthController: POST /auth/login (credentials)
    AuthController->>AuthService: authenticate(credentials)
    AuthService->>DB: find user by email
    DB-->>AuthService: UserEntity
    AuthService->>JwtUtils: generateToken(UserEntity)
    JwtUtils-->>AuthService: JWT token
    AuthService-->>AuthController: token
    AuthController-->>Client: 200 OK + JWT

```

---

## 6. Entity Relationship Diagram


```mermaid
erDiagram
    USER_ENTITY ||--o{ USER_ROLES : has
    ROLE_ENTITY ||--o{ USER_ROLES : assigned_to
    STOCK_ENTITY ||--o{ WASTE_ENTITY : tracks
    STOCK_ENTITY ||--o{ ORDER_ITEM_ENTITY : contains
    ORDER_ENTITY ||--o{ ORDER_ITEM_ENTITY : has
    
    USER_ENTITY {
        Long id PK
        String username UK
        String email UK
        String password
        LocalDateTime createdAt
    }
    
    ROLE_ENTITY {
        Long id PK
        String name UK
    }
    
    USER_ROLES {
        Long user_id FK
        Long role_id FK
    }
    
    STOCK_ENTITY {
        Long id PK
        String name UK
        Double currentStock
        Double minimumStock
        String unit
        LocalDateTime lastUpdate
    }
    
    WASTE_ENTITY {
        Long id PK
        Long ingredient_id FK
        Double quantity
        String unit
        String reason
        String details
        LocalDateTime timestamp
    }
    
    ORDER_ENTITY {
        Long id PK
        LocalDateTime orderDate
        Integer itemCount
        String notes
        String status
    }
    
    ORDER_ITEM_ENTITY {
        Long id PK
        Long order_id FK
        Long stock_id FK
        BigDecimal quantity
        String unit
        BigDecimal unitPrice
    }
```
## 7. API

> Base path: `/api`

| Method | Endpoint | Auth | Description |
|--------|-----------|------|-------------|
| POST | `/api/register` | Public | Register a new user |
| POST | `/api/auth/token` | Basic | Get JWT token |
| GET | `/api/stock` | JWT | Get all stock items |
| POST | `/api/stock` | JWT | Create stock item |
| PUT | `/api/stock/{id}` | JWT | Update stock item |
| DELETE | `/api/stock/{id}` | JWT | Delete stock item |
| POST | `/api/waste` | JWT | Register waste |
| DELETE | `/api/waste/{id}` | JWT | Delete waste |
| GET | `/api/orders` | JWT | Get recommended orders |
| POST | `/api/orders` | JWT | Create new order |
| GET | `/api/analytics/stats` | JWT | Get analytics stats |

---

## 8. Security

- JWT tokens expire in 1 hour.
- Passwords encoded with BCrypt.
- Roles handled by `RoleEntity` (`USER`, `ADMIN`).
- `SecurityConfig` uses stateless JWT authentication.
- `api-endpoint` property allows configurable base path.

---

## 9. Test

<img width="262" height="593" alt="Screenshot 2025-10-30 at 12 01 15" src="https://github.com/user-attachments/assets/1247cac1-b971-42e1-bdae-8a1533a8884c" />


---

## 10. Author

- **Paula** 

## Releated Projects 
- **Stockee Frontend** - [React Frontend Repository](https://github.com/Paulafrdz/stockee-frontend)




