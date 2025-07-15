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
- Multipais configuration (`APP_ENV` and `APP_REGION`).
- In-memory database H2 for persistence.
- Redis cache for optimized retrieval.
- Full reactive stack using WebFlux and R2DBC.
- Clean separation between application, domain, and infrastructure layers (Hexagonal Architecture + DDD).

---

## 🛠️ Technical Decisions

1. **Hexagonal Architecture & DDD**  
   The project follows a hexagonal architecture, clearly separating domain, application, and infrastructure concerns, ensuring modularity, testability, and flexibility to adapt to future business changes.


2. **Redis Cache Strategy**  
   I considered several caching strategies:
- Caching by `(productId, brandId, applicationDate)` would cause high cache fragmentation, increasing memory usage and cache misses if requests vary in date.
- Caching only by `(productId, brandId)` reduces the number of cache keys and improves reusability across requests.

👉 **Why this choice?**  
For this MVP-oriented implementation, I opted to cache all prices by `(productId, brandId)` and filter in-memory by date and priority. This avoids premature optimization and keeps the solution flexible and simple.

I am aware this approach could become a bottleneck in scenarios with millions of records for the same product/brand combination, as database queries would load large datasets and filtering would happen in memory. However, I made a **pragmatic trade-off**, based on realistic product life cycles, where such volume is unlikely in short-to-mid term.

📌 **Improvement opportunity:**  
In a production-grade system, the main improvement would be to **delegate filtering to the database**, e.g., using SQL filtering by date and ordering by priority. This could be paired with optimized indexes and a more granular caching strategy depending on real traffic patterns (e.g., by date ranges or recent records).


3. **Error Handling**  
   Graceful fallback to database query when Redis cache misses or fails.


4. **Multicountry Support**  
   Configurations and seed data are fully environment-dependent using `APP_ENV` and `APP_REGION` variables, supporting flexible multi-environment, multi-country deployments.

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

✅ Full coverage on domain mappers, services, Redis, and H2 repositories.

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

Thanks for reviewing!

---

**Author:** Arturo Barbero Pérez 👨‍💻

