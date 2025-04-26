# Pricing API Microservice 

---

This project was built using **Spring Boot 3**, **Reactor**, **Redis**, and **R2DBC** on **H2** database. It follows **Hexagonal Architecture** and **DDD principles**, ensuring a modular, scalable, and easily testable design.

---

## 📚 Table of Contents
- [Overview](#overview)
- [Technical Decisions](#technical-decisions)
- [How to Run Locally](#how-to-run-locally)
- [Swagger OpenAPI Documentation](#swagger-openapi-documentation)
- [Testing Strategy](#testing-strategy)
- [Future Improvements](#future-improvements)

---

## 🧠 Overview
This microservice retrieves the applicable price for a given product and brand at a specific application date. It supports:
- Multipais configuration (`APP_ENV` and `APP_REGION`).
- In-memory database H2 for persistence.
- Redis cache for optimized retrieval.
- Full reactive stack using WebFlux and R2DBC.
- Clean separation between application, domain, and infrastructure layers (Hexagonal Architecture + DDD).

---

## 🛠️ Technical Decisions

**1. Hexagonal Architecture & DDD**
- The project is structured by separating domain, application, and infrastructure concerns, enabling high modularity and testability.

**2. Redis Cache Strategy**
- Initially, the idea was to cache based on (`productId`, `brandId`, `applicationDate`). However, that would cause a lot of **cache misses** because every different date would be a different cache key, leading to poor cache hit ratios.
- Instead, I cache **only** (`productId`, `brandId`) as the key. I retrieve all applicable prices for that combination and **filter in-memory by date**. This optimizes Redis usage without significantly impacting performance.

**3. Error Handling**
- Fall back to database query when cache fails.

**4. Multipais Support**
- The configuration files and database initial data are environment-dependent: `APP_ENV` and `APP_REGION`.
- Example: `data/prod/es/large_entries.json` for Production in Spain.

---

## 🚀 How to Run Locally

### Prerequisites
- Docker 🐳
- Docker Compose

### Steps

```bash
# 1. Build the project
mvn clean install

# 2. Run docker-compose
docker-compose up --build
```

This will spin up:
- Redis server (port 6379)
- Pricing API Microservice (port 8080)

### Postman Collection
A **Postman collection** has been attached in the project!
It includes normal test cases and **stress tests**.

### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T18:30:00&productId=35455&brandId=1"
```

This will return either:
- **200 OK** with applicable price.
- **204 No Content** if no applicable price is found.

---

## 📖 Swagger OpenAPI Documentation

Once the service is running, you can view the API documentation:

👉 [http://localhost:8080/docs](http://localhost:8080/docs)

Includes:
- GET /api/v1/prices with parameters.
- Response examples.

---

## 🧪 Testing Strategy

- **Unit tests** (using JUnit 5 and Mockito).
- **Integration tests** (using WebTestClient).
- **Coverage** exceeds 85% 📈.
- **Stress tests** included in the Postman collection.

✅ Full coverage on domain mappers, services, Redis, and H2 repositories.

---

## 🚀 Future Improvements

- **Implement Circuit Breaker (Resilience4j)**:
    - In case Redis or H2 fails, automatically fallback or open a circuit.
- **Distributed Cache**:
    - Use Redis Cluster for horizontal scalability.
- **Redis Event Listeners**:
    - Automatically evict or refresh cache when H2 changes.
- **Metrics and Monitoring**:
    - Add Prometheus/Grafana integration.
- **Retry Mechanism**:
    - Auto-retry transient failures (e.g., in Redis or DB) using reactive retries.
- **API Rate Limiting**:
    - Protect the API against overload using Bucket4j or Spring Cloud Gateway.

---
