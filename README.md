# Expense Tracker Backend

A Spring Boot backend application for managing daily expenses with JWT authentication, MySQL database, and category-wise analytics.

## 🚀 Features
- User Registration & Login (JWT)
- Add, Update & Delete Expenses
- Category-wise Expense Summary
- Total Monthly Expense Dashboard (optimized using efficient data structures)
- Spring Security Authentication & Authorization
- Complete REST API architecture with request/response validation
- Swagger UI Integration for API testing
- Exception Handling (Global, Custom)
- MySQL Database Integration

## 🛠️ Tech Stack
- **Java 11**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security + JWT**
- **MySQL**
- **Maven**
- **Swagger / Postman**

## 📂 Project Structure
```
src  
 ├── main  
 │   ├── java  
 │   │   └── com.example.expensetracker  
 │   ├── resources  
 │   │   ├── application.properties  
 │   │   └── schema.sql  
 └── test  
```

## 🔐 Authentication Flow (JWT)
1. User registers → Backend stores hashed password
2. User logs in → Backend generates JWT token
3. Client sends token in `Authorization: Bearer <token>`
4. Secured endpoints validate token & allow access

## 📊 Dashboard Calculations
- Computes total expenses for a given month
- Groups expenses by category
- Uses optimized loops & maps (DSA-based approach) for fast calculations
- Perfect for analytics and insights

## 📄 API Documentation
Swagger UI available at:
```
/swagger-ui/index.html
```

## 🧪 Testing Tools
- Postman Collections
- Swagger UI

## ▶️ Running the Project

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

## ❤️ Contributing
Pull requests are welcome.

## 📬 Contact
For any queries:  
**Rakesh Ariveni**  
GitHub: https://github.com/Rakesh-Ariveni

