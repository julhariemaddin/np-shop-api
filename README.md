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

## ▶︎ Quick Start

```bash
git clone https://github.com/julhariemaddin/np-shop.git
cd np-shop

# create your .env file (see Environment Variables below)
cp .env.example .env

docker compose -f Docker-compose.yml up -d --build
```

The API is now running at `http://localhost:8080`.

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

## ⌂ Architecture

NP-Shop follows a **layered architecture** with clear separation of concerns — each layer only talks to the one directly below it:

```
Controller  →  Service  →  Repository  →  Database
```

> This is a **layered Spring architecture**, not strict Clean Architecture / Hexagonal Architecture — there's no dedicated domain layer that's fully isolated from framework concerns (entities are JPA-annotated, services depend on Spring beans directly, etc.). For a project this size, layered architecture is the right level of structure: it keeps responsibilities separated without the overhead of ports/adapters.

```mermaid
flowchart TD
    Client[Client] -->|HTTP request| RateLimit[Rate Limit Filter]
    RateLimit --> JwtFilter[JWT Authentication Filter]
    JwtFilter --> Controller[Controller Layer]

    Controller --> Service[Service Layer]

    Service --> Repository[Repository Layer<br/>Spring Data JPA]
    Repository --> DB[(PostgreSQL)]

    Service --> RedisService[Redis Services<br/>Cart / Refresh Tokens]
    RedisService --> Redis[(Redis)]

    Service --> PayPalService[PayPal Service]
    PayPalService --> PayPalAPI[PayPal API]
    PayPalAPI -. webhook .-> Webhook[Paypal Webhook Controller]
    Webhook --> Service

    JwtFilter -. validates .-> JwtService[JWT Service]
    RateLimit -. reads token via .-> JwtService
```

**Flow summary:**
1. Every request passes through the **Rate Limit Filter** (account/IP/endpoint buckets via Redisson) and the **JWT Authentication Filter** (validates the bearer token and loads the account).
2. The **Controller** receives the validated request and delegates to a **Service**.
3. The **Service** holds the business logic and talks to:
   - **Repository** (Spring Data JPA) for persistent data in **PostgreSQL**
   - **Redis services** for ephemeral data (cart contents, refresh tokens)
   - **PayPal service** for checkout sessions, with PayPal calling back into the **webhook controller** to confirm payment status

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

## ⌁ API Examples

### `POST /api/auth/register`

Request:
```json
{
  "username": "julharie",
  "password": "StrongPass1!",
  "email": "julharie@example.com"
}
```

Response `200 OK`:
```json
{
  "id": "f3a1c9e2-1b2d-4c3e-8a9f-2d3e4f5a6b7c",
  "username": "julharie",
  "email": "julharie@example.com"
}
```

### `POST /api/auth/sign-in`

Request:
```json
{
  "username": "julharie",
  "password": "StrongPass1!"
}
```

Response `200 OK`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "8f14e45f-ceea-467e-bd9d-1a2b3c4d5e6f",
  "username": "julharie",
  "role": ["ROLE_USER"],
  "email": "julharie@example.com"
}
```

### `GET /api/v1/product`

Response `200 OK` (paginated):
```json
{
  "content": [
    {
      "id": "9b2c1d3e-...",
      "name": "Wireless Mouse",
      "description": "Ergonomic 2.4GHz wireless mouse",
      "stock": 42,
      "price": 19.99,
      "mainImage": { "url": "https://.../mouse.jpg" },
      "categoryId": "c1a2b3...",
      "numberOfReviews": 12,
      "overAllRating": 4.5
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### `POST /api/v1/order`

Requires `Authorization: Bearer <token>`. Creates an order from the account's current Redis cart — no request body needed.

Response `200 OK`:
```json
{
  "id": "a1b2c3d4-...",
  "status": "PENDING",
  "totalItemsQuantity": 2,
  "totalPrice": 39.98,
  "createdAt": "2026-06-23T10:15:00"
}
```

Rate-limited response `429 Too Many Requests`:
```json
{
  "code": 429,
  "message": "Too many requests"
}
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

### Environment Variables

| Variable | Used by | Description |
|---|---|---|
| `JWT_SECRET` | All profiles | Secret key used to sign/verify JWTs |
| `JWT_TOKEN_EXPIRATION` | All profiles | Access token lifetime (ms) |
| `PAYPAL_CLIENT_ID` | All profiles | PayPal app client ID |
| `PAYPAL_CLIENT_SECRET` | All profiles | PayPal app client secret |
| `PAYPAL_WEBHOOK_ID` | All profiles | PayPal webhook ID for signature verification |
| `PAYPAL_IS_SANDBOX` | All profiles | `true` for sandbox, `false` for production |
| `PAYPAL_RETURN_URL` | All profiles | Redirect URL after successful checkout |
| `PAYPAL_CANCEL_URL` | All profiles | Redirect URL after cancelled checkout |
| `DOCK_JDBC_POSTGRES_DB` | `docker` profile | Postgres JDBC URL (e.g. `jdbc:postgresql://postgres:5432/mydb`) |
| `DOCK_POSTGRES_USERNAME` | `docker` profile | Postgres username |
| `DOCK_POSTGRES_PASSWORD` | `docker` profile | Postgres password |
| `DOCK_REDIS_PASSWORD` | `docker` profile | Redis password |
| `DEV_JDBC_POSTGRES_DB` | `dev` profile | Postgres JDBC URL for local development |
| `DEV_POSTGRES_USERNAME` | `dev` profile | Postgres username (local) |
| `DEV_POSTGRES_PASSWORD` | `dev` profile | Postgres password (local) |
| `DEV_REDIS_PASSWORD` | `dev` profile | Redis password (local) |

`.env.example` (copy to `.env` and fill in):

```env
JWT_SECRET=
JWT_TOKEN_EXPIRATION=

PAYPAL_CLIENT_ID=
PAYPAL_CLIENT_SECRET=
PAYPAL_WEBHOOK_ID=
PAYPAL_IS_SANDBOX=true
PAYPAL_RETURN_URL=
PAYPAL_CANCEL_URL=

DOCK_JDBC_POSTGRES_DB=jdbc:postgresql://postgres:5432/mydb
DOCK_POSTGRES_USERNAME=admin
DOCK_POSTGRES_PASSWORD=password
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

## ⛁ Deployment

For local Docker usage, see **Quick Start** above. For longer-running or server deployments, run detached and check status/logs separately:

```bash
# Start in the background
docker compose -f Docker-compose.yml up -d --build

# Check container status
docker compose -f Docker-compose.yml ps

# Tail logs
docker compose -f Docker-compose.yml logs -f np-shop

# Stop everything
docker compose -f Docker-compose.yml down
```

The API will be available at `http://localhost:8080`.

> For production, swap the hardcoded Postgres/Redis credentials in `Docker-compose.yml` for `.env`-driven values (see the note in **Configuration** above), and consider a managed Postgres/Redis instance instead of the bundled containers.

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

## ☐ Screenshots


**Docker containers running:**
```
docker compose -f Docker-compose.yml ps
```
![Docker containers](docs/screenshots/docker-ps.png)

**Database schema (DBeaver ER view):**
![Database diagram](docs/screenshots/db-diagram.png)

**API documentation (Swagger UI):**
> Not currently included in `pom.xml`. To add it:
> ```xml
> <dependency>
>     <groupId>org.springdoc</groupId>
>     <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
>     <version>2.6.0</version>
> </dependency>
> ```
> Once added, Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.
> ![Swagger UI](docs/screenshots/swagger-ui.png)

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
