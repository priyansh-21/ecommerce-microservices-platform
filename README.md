# 🛍️ Microservices-Style E-Commerce Platform

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)](LICENSE)

A backend e-commerce platform built with **Spring Boot**, split into four clean modules —
**User, Product, Order, and Payment** — with JWT authentication, role-based access control,
Redis-backed caching, and a fully containerized deployment setup.

> Built by [Priyansh Gautam](https://github.com/priyansh-21) — B.Tech CSE, Medicaps University

---

## 🏗️ Architecture

```
                       ┌───────────────────────┐
                       │      API Gateway       │
                       │   (Spring Security +   │
                       │      JWT Filter)       │
                       └───────────┬───────────┘
                                   │
        ┌──────────────┬──────────┼──────────┬──────────────┐
        │              │          │          │              │
   ┌────▼────┐   ┌─────▼────┐ ┌──▼───┐  ┌────▼─────┐
   │  Auth/  │   │ Product  │ │Order │  │ Payment  │
   │  User   │   │  Module  │ │Module│  │  Module  │
   └────┬────┘   └─────┬────┘ └──┬───┘  └────┬─────┘
        │              │         │           │
        └──────────────┴────┬────┴───────────┘
                             │
                  ┌──────────▼──────────┐
                  │  PostgreSQL + Redis  │
                  └──────────────────────┘
```

Each module is organized as an isolated Java package with its own entity, repository,
service, and controller — making it straightforward to later split into truly
independent, physically separate microservices (one JAR + one deployment per module)
without touching business logic.

## ✨ Features

- **JWT Authentication** — stateless auth with 1-hour token expiry
- **Role-Based Access Control** — `USER` / `ADMIN` roles enforced at the route level
- **Redis Caching** — product reads cached to cut DB latency (falls back to in-memory cache if Redis isn't configured)
- **Order → Payment workflow** — stock is decremented transactionally when an order is placed, order status flows `CREATED → PAID → SHIPPED → DELIVERED`
- **Centralized error handling** — consistent JSON error responses
- **Health checks & metrics** — Spring Actuator + Prometheus-ready `/actuator/prometheus` endpoint
- **Dockerized** — multi-stage Dockerfile + docker-compose for one-command local spin-up

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3, Spring Security, Spring Data JPA |
| Auth | JWT (jjwt) |
| Database | PostgreSQL (production), H2 (local/dev default) |
| Cache | Redis |
| Monitoring | Spring Actuator, Prometheus endpoint |
| Containerization | Docker, Docker Compose |

## 📁 Project Structure

```
ecommerce-microservices/
├── src/main/java/com/priyansh/ecommerce/
│   ├── auth/          # Register/Login + JWT issuing
│   ├── user/           # User entity & profile endpoints
│   ├── product/       # Product CRUD + Redis caching
│   ├── order/          # Order placement & lifecycle
│   ├── payment/       # Payment processing
│   ├── security/       # JWT filter & utilities
│   ├── config/         # Security config, admin seeder
│   └── common/         # Global exception handling
├── src/main/resources/application.yml
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🚀 Running Locally

### Option 1 — Quick start (H2 in-memory DB, no Docker needed)
```bash
mvn spring-boot:run
```
The app starts on `http://localhost:8080` with an in-memory database and in-memory
cache — nothing else to install. A default admin user (`admin` / `admin123`) is
seeded automatically.

### Option 2 — Full stack with Docker Compose (Postgres + Redis)
```bash
docker-compose up --build
```

## 🔑 API Reference

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Log in, returns JWT |
| GET | `/api/users/me` | Authenticated | Get current user profile |
| GET | `/api/products` | Public | List all products |
| POST | `/api/products` | Admin | Create a product |
| PUT | `/api/products/{id}` | Admin | Update a product |
| DELETE | `/api/products/{id}` | Admin | Delete a product |
| POST | `/api/orders` | Authenticated | Place an order |
| GET | `/api/orders/me` | Authenticated | List my orders |
| PUT | `/api/orders/{id}/status` | Authenticated | Update order status |
| POST | `/api/payments` | Authenticated | Pay for an order |
| GET | `/actuator/health` | Public | Health check |

### Example: Register + Login
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
```

### Example: Create a Product (admin token required)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Mouse","description":"Ergonomic","price":19.99,"stock":100,"category":"Electronics"}'
```

## ⚙️ Environment Variables

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8080` | Server port |
| `DATABASE_URL` | `jdbc:h2:mem:ecommerce` | JDBC connection string |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | `sa` / _(empty)_ | DB credentials |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | Redis connection |
| `CACHE_TYPE` | `simple` | Set to `redis` to enable Redis caching |
| `JWT_SECRET` | _dev default_ | **Change in production** — 32+ char secret |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | `admin` / `admin123` | Seeded admin account |

## 🗺️ Roadmap
- [ ] Split into physically separate Spring Boot services + API Gateway
- [ ] Kubernetes manifests with HPA (CPU/memory autoscaling)
- [ ] Circuit breaker (Resilience4j) between Order → Payment
- [ ] Full JMeter load-test suite

## 📄 License
MIT
