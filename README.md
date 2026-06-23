<div align="center">

# ⚙︎ NP-Shop API

**A Spring Boot e-commerce backend with JWT auth, Redis-backed rate limiting, PayPal payments, and PostgreSQL persistence.**

[![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Redisson](https://img.shields.io/badge/Redisson-FF6600?style=for-the-badge&logo=redis&logoColor=white)](https://redisson.org/)
[![JWT](https://img.shields.io/badge/JWT-JJWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://github.com/jwtk/jjwt)
[![PayPal](https://img.shields.io/badge/PayPal-SDK-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://developer.paypal.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Lombok](https://img.shields.io/badge/Lombok-CC0000?style=for-the-badge&logo=lombok&logoColor=white)](https://projectlombok.org/)

</div>

---

## ✎ Overview

**NP-Shop** is a REST API for an e-commerce platform built with **Spring Boot 3.5.14** and **Java 21**. It handles accounts/roles, products & categories, image uploads, reviews, a Redis-backed shopping cart, order processing, and PayPal checkout — all sitting behind a custom **JWT authentication layer** and a **Redis-powered sliding-window rate limiter**.

---

## ⚒︎ Tech Stack

| Layer | Technology |
|---|---|
| **Language / Runtime** | ![Java](https://img.shields.io/badge/-Java%2021-007396?style=flat-square&logo=openjdk&logoColor=white) |
| **Framework** | ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot%203.5.14-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/-Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) ![Spring Data JPA](https://img.shields.io/badge/-Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white) |
| **Database** | ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-336791?style=flat-square&logo=postgresql&logoColor=white) |
| **Cache / Rate Limiting** | ![Redis](https://img.shields.io/badge/-Redis-DC382D?style=flat-square&logo=redis&logoColor=white) ![Redisson](https://img.shields.io/badge/-Redisson-FF6600?style=flat-square&logo=redis&logoColor=white) |
| **Auth** | ![JWT](https://img.shields.io/badge/-JJWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white) |
| **Payments** | ![PayPal](https://img.shields.io/badge/-PayPal%20Server%20SDK-00457C?style=flat-square&logo=paypal&logoColor=white) |
| **Build Tool** | ![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white) |
| **Boilerplate** | ![Lombok](https://img.shields.io/badge/-Lombok-CC0000?style=flat-square&logo=lombok&logoColor=white) |
| **Containerization** | ![Docker](https://img.shields.io/badge/-Docker-2496ED?style=flat-square&logo=docker&logoColor=white) ![Docker Compose](https://img.shields.io/badge/-Docker%20Compose-2496ED?style=flat-square&logo=docker&logoColor=white) |
| **Validation** | ![Hibernate Validator](https://img.shields.io/badge/-Bean%20Validation-59666C?style=flat-square&logo=hibernate&logoColor=white) |

---

## ✦ Features

- ⚿ **JWT-based authentication** — register / sign-in, refresh tokens stored in Redis
- ▣ **Product catalog** — categories, images, search, reviews & auto-calculated ratings
- ⛁ **Redis-backed cart** — fast, ephemeral cart storage per account
- ☐ **Order management** — order items, pending-payment expiry scheduler
- ⌁ **PayPal integration** — checkout flow + webhook handling
- ⧗ **Custom rate limiting** — per-account, per-IP, and per-endpoint limits via Redisson
- ✎ **Account/profile management** — update profile & password
- ⚙︎ **Dockerized** — multi-stage build, Compose stack with Postgres + Redis

---

## ▣ Entity Relationship Diagram

```mermaid
erDiagram
    ACCOUNT {
        UUID id PK
        string username
        string password
        string email
        datetime createdAt
    }
    ROLE {
        UUID id PK
        string name
    }
    CATEGORY {
        UUID id PK
        string categoryName
    }
    PRODUCT {
        UUID id PK
        string name
        string description
        int stock
        int reserveStock
        double price
        double overAllRating
        string mainImageUrl
        UUID category_id FK
        datetime createdAt
    }
    IMAGE {
        UUID id PK
        string url
        string fileName
        string contentType
        UUID product_id FK
    }
    REVIEW {
        UUID id PK
        int rating
        string description
        datetime createdAt
        UUID product_id FK
        UUID account_id FK
    }
    ORDERS {
        UUID id PK
        int totalItemsQuantity
        string status
        datetime createdAt
        datetime expiredAt
        UUID account_id FK
        UUID payment_id FK
    }
    ORDER_ITEM {
        UUID id PK
        UUID order_id FK
        UUID productId
        string productName
        boolean productStatus
        int quantity
        double price
    }
    PAYMENT {
        UUID id PK
        string paymentId
        string status
        double totalPrice
        UUID order_id FK
    }

    ACCOUNT ||--o{ ACCOUNT_ROLES : has
    ROLE ||--o{ ACCOUNT_ROLES : assigned_to
    ACCOUNT_ROLES {
        UUID account_id FK
        UUID roles_id FK
    }
    ACCOUNT ||--o{ ORDERS : places
    ACCOUNT ||--o{ REVIEW : writes
    CATEGORY ||--o{ PRODUCT : groups
    PRODUCT ||--o{ IMAGE : has
    PRODUCT ||--o{ REVIEW : receives
    ORDERS ||--o{ ORDER_ITEM : contains
    ORDERS ||--|| PAYMENT : has
```

> `ACCOUNT_ROLES` is the JPA `@ManyToMany` join table between `Account` and `Role`.

---

## ⇉ Application Request Flow

```mermaid
flowchart TD
    Client[Client Request] --> Filter

    subgraph Filter["Rate Limiting Filter Layer"]
        direction TB
        F1[Collect Information] --> F2[JWT Service: extract account ID from token]
        F1 --> F3[Get path URI]
        F1 --> F4[Get IP address]
        F2 --> F5[Get account ID]
        F5 --> F6[Pass to Rate Limit Service]
        F3 --> F6
        F4 --> F6
        F6 --> F7{Allowed?}
        F7 -->|No| F8[429 Too Many Requests]
        F7 -->|Yes| F9[Proceed to Spring Security filter chain]
    end

    subgraph Service["Rate Limiting Service Layer"]
        direction TB
        S1{Account ID present?} -->|Yes| S2[Key: rl:account:&lt;id&gt; — 100 req/min]
        S1 -->|No| S3[Key: rl:ip:&lt;ip&gt; — 10 req/min]
        S2 --> S4{Hitting a protected endpoint?}
        S3 --> S4
        S4 -->|/api/auth/sign-in| S5[5 req/min]
        S4 -->|/api/auth/register| S6[10 req/min]
        S4 -->|No| S7[Allowed]
    end

    F6 --> S1
    S5 --> F7
    S6 --> F7
    S7 --> F7

    F9 --> Auth[JWT Authentication Filter]
    Auth --> Controllers[Controllers: Auth / Product / Cart / Order / Payment / User]
    Controllers --> DB[(PostgreSQL)]
    Controllers --> Cache[(Redis: Cart / Refresh Tokens / Rate Limit Keys)]
    Controllers --> PayPal[PayPal API]
```

This mirrors the rate-limiting diagram for the project: the **filter layer** gathers the account ID (from the JWT, if present), the request path, and the IP; it then hands those off to the **service layer**, which checks an account- or IP-level bucket first, and — only for `/api/auth/sign-in` and `/api/auth/register` — an additional per-endpoint bucket, all backed by Redisson `RRateLimiter`s.

---

## ⧗ Rate Limiting

| Scope | Key prefix | Limit |
|---|---|---|
| Authenticated account (global) | `rl:account:<accountId>` | 100 requests / minute |
| Unauthenticated IP (global) | `rl:ip:<ip>` | 10 requests / minute |
| `POST /api/auth/sign-in` | `rl:endpoint:<accountId or ip>` | 5 requests / minute |
| `POST /api/auth/register` | `rl:endpoint:<accountId or ip>` | 10 requests / minute |

If a token is present, the user's ID is extracted from the JWT and rate-limited by **account**. If not, the request falls back to **IP-based** limiting. Either way, exceeding a bucket returns:

```json
{ "code": 429, "message": "Too many requests" }
```

---

## ⌁ API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new account |
| `POST` | `/api/auth/sign-in` | Authenticate and receive a JWT |
| `POST` | `/request` | Refresh access token |
| `GET` | `/api/users/me` | Get current profile |
| `PATCH` | `/api/users/me/profile` | Update profile |
| `PATCH` | `/api/users/me/password` | Update password |
| `GET/POST/PUT/DELETE` | `/api/v1/product` | Product CRUD, search, reviews |
| `GET/POST/PUT/DELETE` | `/api/v1/category` | Category CRUD |
| `GET/POST/DELETE` | `/api/v1/cart` | Cart management (Redis-backed) |
| `GET/POST/DELETE` | `/api/v1/order` | Order placement & retrieval |
| `GET/POST/DELETE` | `/api/v1/image` | Product image upload/retrieval |
| `*` | `/api/v1/paypal/**` | PayPal checkout + webhook |
| `GET` | `/api/v1/server/check` | Health check |

---

## ⚿ Configuration

The app uses Spring profiles: **`dev`** (local) and **`docker`** (containerized), both reading secrets from environment variables.

### Required environment variables

```env
# JWT
JWT_SECRET=
JWT_TOKEN_EXPIRATION=

# PayPal
PAYPAL_CLIENT_ID=
PAYPAL_CLIENT_SECRET=
PAYPAL_WEBHOOK_ID=
PAYPAL_IS_SANDBOX=true
PAYPAL_RETURN_URL=
PAYPAL_CANCEL_URL=

# Postgres (docker profile)
DOCK_JDBC_POSTGRES_DB=jdbc:postgresql://postgres:5432/mydb
DOCK_POSTGRES_USERNAME=admin
DOCK_POSTGRES_PASSWORD=password

# Redis (docker profile)
DOCK_REDIS_PASSWORD=StrongPassword
```

> **Note:** The Postgres/Redis container **definitions** in `Docker-compose.yml` currently set their own credentials directly (`POSTGRES_PASSWORD: password`, `--requirepass StrongPassword`). For these to come from `.env` too, reference the same variables there, e.g.:
> ```yaml
> redis:
>   command: ["redis-server", "--requirepass", "${DOCK_REDIS_PASSWORD}"]
> postgres:
>   environment:
>     POSTGRES_PASSWORD: ${DOCK_POSTGRES_PASSWORD}
> ```

---

## ⛁ Running with Docker

```bash
# 1. Create a .env file in the project root with the variables above

# 2. Build and start everything (app + Postgres + Redis)
docker compose -f Docker-compose.yml up --build
```

The API will be available at `http://localhost:8080`.

---

## ▶︎ Running Locally (dev profile)

```bash
# Requires a running local Postgres and Redis instance
export DEV_JDBC_POSTGRES_DB=jdbc:postgresql://localhost:5432/mydb
export DEV_POSTGRES_USERNAME=admin
export DEV_POSTGRES_PASSWORD=password
export DEV_REDIS_PASSWORD=yourpassword
export JWT_SECRET=changeme
export JWT_TOKEN_EXPIRATION=3600000

./mvnw spring-boot:run
```

---

## ⌂ Project Structure

```
np-shop/
├── controller/        # REST controllers (auth, products, cart, orders, users...)
├── service/            # Business logic (products, orders, categories, payments)
├── redis/              # Redis-backed services (cart, refresh tokens)
├── rate_limit/         # RateLimitFilter + RateLimitService (Redisson)
├── security/           # JWT service, filters, account details
├── payment/paypal/     # PayPal SDK config, service, controller, webhook
├── entity/             # JPA entities
├── repo/                # Spring Data JPA repositories
├── dto/                # Request/response DTOs
├── exception/          # Custom exceptions + global handler
└── scheduler/          # Pending-payment order expiry job
```

---

## ✎ Author

**Julharie M. Maddin**
[GitHub @julhariemaddin](https://github.com/julhariemaddin)

---

<div align="center">

Built with ☕︎ & ⚙︎ Spring Boot

</div>
