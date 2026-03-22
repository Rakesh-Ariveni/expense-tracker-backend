# 🚀 Expense Tracker Backend (Production-Ready)

A **production-grade Spring Boot backend system** designed for managing personal expenses with a strong focus on **scalability, reliability, and observability**.

Unlike a basic CRUD application, this project incorporates **real-world backend engineering practices** such as caching, rate limiting, idempotency, structured logging, monitoring, and containerized deployment.

---

# 🔥 Key Highlights

* 🔐 Secure JWT-based authentication
* ⚡ Redis caching for performance optimization
* 🛡 Rate limiting & idempotency for reliability
* 📊 Observability using logs, metrics & health checks
* 🐳 Dockerized deployment with Docker Compose
* ☁️ Deployed on AWS EC2 with RDS

---

# 🧱 Core Features

## 🔐 Authentication & Security

* User Registration & Login using JWT
* Stateless authentication with Spring Security
* Secure APIs using `Authorization: Bearer <token>`
* Centralized JWT filter and security configuration

---

## 💸 Expense Management

* Add, Update, Delete expenses
* User-specific data isolation (multi-user safe)
* Category-based expense tracking
* Strong ownership validation

---

## 📄 Pagination, Sorting & Filtering

Implemented at **database level for performance**

✔ Pagination
✔ Sorting (any column, asc/desc)
✔ Filtering:

* By category
* By date range

### Query Params Supported:

```
page, size, sortBy, direction, category, from, to
```

---

## 📊 Dashboard Analytics

Optimized dashboard API providing:

* Total expenses per user
* Category-wise aggregation (`SUM`, `GROUP BY`)
* Top spending category
* Most frequent category
* Recent transactions (paginated)
* Max spending streak (in-memory logic)

---

# ⚡ Performance & Scalability Enhancements

## 🚀 Redis Caching

* Integrated Redis for caching dashboard responses
* Reduced database load significantly
* Improved response time for read-heavy APIs

---

## 🗄 Database Optimization

* Applied indexing on frequently queried columns
* Improved query performance for filtering & sorting

---

## 🔄 Async Processing

* Used asynchronous processing for non-blocking operations
* Improved API responsiveness

---

# 🛡 Reliability & System Safety

## 🚫 Rate Limiting

* Implemented request throttling (Token Bucket Algorithm)
* Prevents abuse and protects APIs

---

## 🔁 Idempotency Support

* Prevents duplicate request processing
* Ensures safe retry mechanisms

---

## ⚠️ Global Exception Handling

* Centralized error handling using `@RestControllerAdvice`
* Consistent API error responses

---

# 📊 Observability (Production-Level)

## 🧾 Structured Logging

* Implemented structured logging across layers
* Correlation ID added for request tracing across logs

---

## 📈 Metrics & Monitoring

* Integrated Micrometer for custom metrics
* Tracked:

  * API requests
  * Errors
  * Cache behavior

---

## ❤️ Health Checks

* Spring Boot Actuator integration
* Endpoint:

```
/actuator/health
```

---

# 🐳 DevOps & Deployment

## Dockerized Application

* Multi-stage Docker build
* Lightweight and portable deployment

---

## Docker Compose Setup

* Orchestrates:

  * Backend service
  * Redis
* Shared network for inter-service communication

---

## ☁️ AWS Deployment

* Backend deployed on EC2
* MySQL hosted on AWS RDS
* Environment-based configuration

---

# 🛠️ Tech Stack

* Java 11
* Spring Boot
* Spring Security + JWT
* Spring Data JPA
* MySQL (AWS RDS)
* Redis
* Docker & Docker Compose
* AWS EC2
* Micrometer (Metrics)
* Swagger / Postman

---

# 📂 Project Structure

```
src
├── config         # Security, Redis, Configurations
├── controller     # REST APIs
├── service        # Business Logic
├── repository     # JPA Repositories
├── entity         # Database Models
├── dto            # Request/Response DTOs
├── exception      # Global Exception Handling
```

---

# 🔐 Authentication Flow

1. User registers → password hashed using BCrypt
2. User logs in → JWT token generated
3. Client sends token in header
4. JWT filter validates and sets authentication context

---

# 🧪 API Documentation

Swagger UI:

```
/swagger-ui/index.html
```

---

# 📌 Sample APIs

### Auth

```
POST /api/auth/register
POST /api/auth/login
```

### Expenses

```
POST /api/expenses
POST /api/categories
GET /api/categories
GET /api/expenses?page=0&size=5
GET /api/expenses?category=Food
```

### Dashboard

```
GET /api/dashboard
```

---

# 🚀 Running the Project

### Local

```
mvn spring-boot:run
```

---

### Docker

```
docker-compose up -d
```

---

# 🔮 Future Enhancements

* Prometheus + Grafana (real-time monitoring)
* API Versioning
* CI/CD pipeline (GitHub Actions)
* Nginx reverse proxy
* Unit Testing(JUNit, Mockito)
* Kafka for event-driven architecture
* Microservices architecture
* AWS S3 integration

---

# 💡 Key Learning Outcome

This project focuses on **building production-ready backend systems**, covering:

* Scalability (Caching, Async)
* Reliability (Rate limiting, Idempotency)
* Observability (Logging, Metrics, Monitoring)
* Deployment (Docker, AWS)

---

# 👨‍💻 Author

**Rakesh Ariveni**
GitHub: https://github.com/Rakesh-Ariveni