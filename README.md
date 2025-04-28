# Pricing API Microservice 

---

## Technologies Used

- Java 21
- Spring Boot 3.4.4 (WebFlux)
- R2DBC (Reactive H2 Database)
- Redis (Reactive Client)
- Docker + Docker Compose
- JUnit 5 + Mockito + Reactor Test (Unit & Integration tests)
- Lombok
- Swagger OpenAPI 3 (available at `/docs`)

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
- Multicountry configuration (`APP_ENV` and `APP_REGION`).
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

**4. Multicountry Support**
- The configuration files and database initial data are environment-dependent: `APP_ENV` and `APP_REGION` and this data contains 1000 different entries to test scalability and concurrency.
- Example: `data/prod/es/large_entries.json` for Production in Spain.

---

## 🚀 How to Run Locally

### Prerequisites
- Docker 🐳
- Docker Compose

### Steps

```bash
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

---

### Javadoc Documentation 🖋

- Every public class and method is documented.
- **To generate the Javadoc:**

```bash
mvn javadoc:javadoc
```

- **Generated output:**
  - HTML pages under `target/site/apidocs/`

- **To open:**
  - Open `target/site/apidocs/index.html` in your browser.

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
- **Coverage** exceeds 80% 📈.
- **Stress tests** included in the Postman collection.

---

## 🚀 Future Improvements

- **Implement Circuit Breaker (Resilience4j)**:
    - In case Redis or H2 fails, automatically fallback or open a circuit.


- **Redis Event Listeners**:
    - Automatically evict or refresh cache when H2 changes.


- **Metrics and Monitoring**:
    - Add Prometheus/Grafana integration.


- **Retry Mechanism**:
    - Auto-retry transient failures (e.g., in Redis or DB) using reactive retries.


- **API Rate Limiting**:
    - Protect the API against overload using Bucket4j or Spring Cloud Gateway.

---

## Final Notes

The project is structured, documented, tested, and ready for real-world adaptation or extension.

I have taken into account the premises that it has to be scalable, resilient, with good performance and prepared as much as possible for a productive environment... many things and advanced design techniques could be added to make all this happen. 

Personally, what I have done has been to try to make a balance between everything I know, what I think the API should have in a first production release and try to meet the deadline agreed with the technical recruiter, but making sure that the main requirements that were asked were covered. The rest of the improvements are in the Future Improvements section.

Thanks for reviewing!

---

**Author:** Arturo Barbero Pérez 👨‍💻

