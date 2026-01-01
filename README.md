# Expense Tracker Backend

A production-ready Spring Boot backend application for managing personal expenses with JWT authentication, pagination, sorting, filtering, dashboard analytics, and AWS deployment.

---

## 🚀 Features

### 🔐 Authentication & Security
- User Registration & Login using JWT
- Stateless authentication with Spring Security
- Secure REST APIs protected via Authorization: Bearer token
- Centralized JWT filter and security configuration

---

### 💸 Expense Management
- Add, Update, Delete expenses
- Each expense is mapped to a specific authenticated user
- Category-based expense tracking
- Strong ownership validation (user-level data isolation)

---

### 📄 Pagination, Sorting & Filtering (System Design – Core)
Implemented **server-side pagination & filtering** using Spring Data JPA.

✔ Pagination  
✔ Sorting (any column, asc/desc)  
✔ Filtering:
- By category
- By date range

**Query parameters supported:**
- `page`
- `size`
- `sortBy`
- `direction`
- `category`
- `from`
- `to`

All filtering and sorting is done **at DB level for performance**.

---

### 📊 Dashboard Analytics
Optimized dashboard API providing:

- Total expenses per user
- Category-wise expense aggregation
- Top spending category
- Most frequent category
- Recent expenses (limited & ordered)
- Max spending streak (in-memory calculation)

**Design approach:**
- Aggregations using `SUM`, `GROUP BY`
- Pagination for recent expenses
- JVM-level logic only where sequential processing is required

---

###☁️ Deployment & DevOps
- Dockerized Spring Boot application
- Deployed on AWS EC2
- MySQL database hosted on AWS RDS
- Environment-based configuration
- Ready for horizontal scalability

---

### 🧪 API Testing & Documentation
- Swagger UI integration
- APIs tested via Postman
- Clean request/response DTO architecture

---

## 🛠️ Tech Stack

- Java 11
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Docker
- AWS EC2
- AWS RDS
- Maven
- Swagger
- Postman

---

## 📂 Project Structure

```
src
├── main
│ ├── java
│ │ └── com.rakesh.expensetracker
│ │ ├── config # Security, JWT, CORS
│ │ ├── controller # REST Controllers
│ │ ├── service # Business Logic
│ │ ├── repository # JPA Repositories
│ │ ├── entity # JPA Entities
│ │ └── dto # Request / Response DTOs
│ └── resources
│ └── application.properties
└── test

---

## 🔐 Authentication Flow (JWT)
1. User registers → password stored using BCrypt hashing
2. User logs in → JWT token generated
3. Client sends token in: Authorization: Bearer <JWT>
4. JWT filter validates token and sets authentication context

---

## 📊 Dashboard Design (System Design Explanation)
- Heavy computations pushed to database layer
- Read-heavy endpoints optimized using pagination
- JVM logic used only for sequential computations
- Architecture is cache-ready for future Redis integration

---

## 📄 API Documentation
Swagger UI available at:

```
/swagger-ui/index.html
```

## 🧪 Sample APIs

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`

### Expenses
- `POST /api/expenses`
- `GET /api/expenses?page=0&size=5`
- `GET /api/expenses?sortBy=amount&direction=asc`
- `GET /api/expenses?category=Food`
- `GET /api/expenses?from=2025-01-01T00:00:00&to=2025-01-31T23:59:59`

### Dashboard
- `GET /api/dashboard`

---

##▶️ Running the Project

### 1️⃣ Clone the repository

```
git clone https://github.com/Rakesh-Ariveni/expense-tracker-backend.git
```

### 2️⃣ Configure MySQL in `application.properties`
```
spring.datasource.url=jdbc:mysql://localhost:3306/expensetracker
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3️⃣ Run the application
```
mvn spring-boot:run
```
---

##☁️ Deployment
- Application packaged using Docker
- Deployed on AWS EC2
- MySQL hosted on AWS RDS
- Production-ready configuration

---

## 📌 Future Enhancements
- Redis caching for dashboard
- Cursor-based pagination
- Rate limiting
- Async processing
- AWS S3 integration
- Observability & monitoring

---

## ❤️ Contributing
Pull requests are welcome.

## 📬 Contact
For any queries:  
**Rakesh Ariveni**  
GitHub: https://github.com/Rakesh-Ariveni
