# 🚀 FinFlow

### Event-Driven Personal Finance Management Platform

A production-grade personal finance platform built using Spring Boot, Kafka, Redis, Docker, AWS, and GitHub Actions.

FinFlow goes beyond traditional expense tracking by combining intelligent budget management, real-time notifications, event-driven analytics, financial insights, and cloud-native deployment practices.

---

# 🔥 Key Highlights

* 🔐 JWT Authentication & Spring Security
* 💰 Intelligent Budget Management System
* ⚡ Event-Driven Architecture using Apache Kafka
* 📨 Real-Time Budget & Threshold Notifications
* 📊 Redis-Powered Analytics Dashboard
* 🔁 Kafka Retry & Dead Letter Queue (DLQ)
* 📅 Weekly & Monthly Financial Summary Emails
* 🧠 Financial Insights & Dashboard Intelligence
* 🧪 Unit Testing & Integration Testing
* 🚀 CI/CD Pipeline using GitHub Actions
* 📈 Monitoring with Prometheus & Grafana
* 🐳 Dockerized Deployment on AWS EC2

---

## 🏗️ Architecture Overview

FinFlow follows an event-driven architecture that combines Spring Boot, Apache Kafka, Redis, MySQL, and cloud-native deployment practices to provide scalable and reliable financial management services.

### Expense Processing Flow

```text
User
  │
  ▼
Spring Boot REST APIs
  │
  ▼
MySQL Database
  │
  ▼
ExpenseCreatedEvent
  │
  ▼
Apache Kafka
  │
  ├──────────────► Analytics Consumer
  │                    │
  │                    ▼
  │              Redis Analytics Store
  │                    │
  │                    ▼
  │              Dashboard APIs
  │
  └──────────────► Notification Consumer
                       │
                       ▼
                Budget Monitoring Engine
                       │
                       ▼
                  Email Notifications
```


### Deployment Flow

```text
Developer Push
      │
      ▼
GitHub Actions
      │
      ▼
Maven Build & Tests
      │
      ▼
Docker Image Build
      │
      ▼
Docker Hub
      │
      ▼
AWS EC2 Deployment
```

### Monitoring Stack

```text
Spring Boot Actuator
          │
          ▼
      Prometheus
          │
          ▼
       Grafana
```

This architecture enables asynchronous processing, real-time analytics, intelligent budget monitoring, distributed caching, and production-grade deployment automation.

---

## 🚀 Core Features

### 🔐 Authentication & Security

* JWT-based Authentication and Authorization
* Spring Security Integration
* Password Encryption using BCrypt
* Redis-based Rate Limiting using Token Bucket Algorithm
* Idempotency Protection for Duplicate Request Prevention
* User-specific Resource Access Control
* Global Exception Handling and Validation

---

### 💸 Expense Management

* Create, Update, Delete, and View Expenses
* Expense Categorization
* Pagination Support
* Sorting Support
* Dynamic Filtering
* User-specific Expense Management
* Expense Analytics Integration

---

### 💰 Budget Intelligence Engine

FinFlow goes beyond traditional expense tracking by providing intelligent budget planning and monitoring capabilities.

#### Budget Types

* Weekly Budgets
* Monthly Budgets
* Yearly Budgets
* Custom Budgets

#### Budget Features

* Budget Allocation Management
* Configurable Warning Thresholds
* Configurable Critical Thresholds
* Budget Utilization Tracking
* Budget Status Monitoring
* Remaining Budget Calculation

#### Category Budget Management

* Category-wise Budget Allocation
* Category Utilization Tracking
* Duplicate Allocation Prevention
* Budget Ownership Validation
* Category Overspending Detection

---

### ⚡ Event-Driven Architecture

Apache Kafka is used to decouple business processes and enable asynchronous event processing.

#### Kafka Components

* Expense Event Producer
* Analytics Consumer
* Notification Consumer
* Multiple Consumer Groups
* JSON Event Serialization

#### Reliability Features

* Retry Mechanism
* Dead Letter Queue (DLQ)
* Error Recovery Handling
* Consumer Isolation

---

### 📊 Analytics Engine

Redis-powered analytics layer designed for real-time dashboard generation.

#### Analytics Metrics

* Total Expense Aggregation
* Expense Count Aggregation
* Category-wise Expense Aggregation
* Real-Time Analytics Updates

#### Redis Analytics Store

* User Expense Totals
* Expense Counts
* Category Spending Metrics
* Fast Dashboard Retrieval

---

### 📨 Notification System

Event-driven notification system powered by Kafka and Redis.

#### Budget Notifications

* Budget Warning Alerts
* Budget Critical Alerts
* Threshold-based Monitoring

#### Category Notifications

* Category Warning Alerts
* Category Fully Utilized Alerts
* Category Exceeded Alerts

#### Reliability

* Redis-based Alert Deduplication
* Duplicate Email Prevention
* Event-driven Processing

---

### 📧 Automated Email Reports

Gmail SMTP-based email notification system.

#### Email Types

* Budget Warning Emails
* Budget Critical Emails
* Category Budget Alerts
* Weekly Financial Summary Reports
* Monthly Financial Summary Reports

#### Scheduled Automation

* Weekly Summary Scheduler
* Monthly Summary Scheduler
* Automated Insight Generation

---

### 📈 Dashboard Intelligence

Comprehensive financial dashboard powered by Redis analytics and real-time aggregations.

#### Financial Insights

* Total Expenses
* Remaining Budget
* Budget Usage Percentage
* Budget Health Status

#### Spending Analytics

* Top Spending Category
* Most Frequent Category
* Highest Expense
* Weekly Spending Trends
* Category-wise Spending Distribution

#### Smart Insights

* Budget Insights
* Spending Insights
* Category Insights
* Overspending Detection
* Warning & Critical Category Detection

---

### 🧪 Testing Strategy

Production-focused testing approach to ensure application reliability.

#### Unit Testing

* Service Layer Testing
* Controller Testing
* Kafka Component Testing
* Analytics Testing
* Budget Module Testing

#### Integration Testing

* End-to-End API Testing
* Database Integration Testing
* Security Validation
* Application Context Validation

---

### 📈 Observability & Monitoring

Production-grade monitoring stack for application health and performance visibility.

#### Monitoring Stack

* Spring Boot Actuator
* Prometheus Metrics Collection
* Grafana Dashboards

#### Custom Metrics

* API Usage Metrics
* Cache Hit Metrics
* Cache Miss Metrics
* Application Health Monitoring

---

### 🚀 DevOps & Cloud Deployment

Modern deployment pipeline with automated build and delivery processes.

#### Containerization

* Docker
* Docker Compose
* Multi-Service Deployment

#### Cloud Infrastructure

* AWS EC2
* Docker Hub Registry
* Automated Image Deployment

#### CI/CD Pipeline

* GitHub Actions
* Automated Builds
* Docker Image Publishing
* Continuous Deployment Workflow

---

## 🛠️ Technology Stack

### Backend

* Java 17
* Spring Boot 3
* Spring MVC
* Spring Data JPA
* Spring Security
* Spring Validation

### Database

* MySQL 8

### Messaging & Event Processing

* Apache Kafka
* Kafka Producers
* Kafka Consumers
* Dead Letter Queue (DLQ)

### Caching & Analytics

* Redis
* Redis Hashes
* Redis-based Rate Limiting
* Redis-based Analytics Engine

### Security

* JWT Authentication
* BCrypt Password Encryption
* Idempotency Protection
* Token Bucket Rate Limiting

### Email Services

* Gmail SMTP
* Spring Mail

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc

### Monitoring & Observability

* Spring Boot Actuator
* Prometheus
* Grafana

### DevOps & Deployment

* Docker
* Docker Compose
* GitHub Actions
* Docker Hub

### Cloud

* AWS EC2

---

## 📂 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.rakesh.expensetracker
│   │
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── config
│   │   ├── exception
│   │   │
│   │   ├── budget
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   ├── repository
│   │   │   ├── entity
│   │   │   └── categoryBudget
│   │   │
│   │   ├── kafka
│   │   │   ├── producer
│   │   │   ├── consumer
│   │   │   ├── event
│   │   │   └── config
│   │   │
│   │   ├── notification
│   │   ├── analytics
│   │   ├── email
│   │   ├── scheduler
│   │   ├── monitoring
│   │   └── security
│   │
│   └── resources
│       ├── application.properties
│       ├── application-prod.properties
│       └── ...
│
└── test
    ├── controller
    ├── service
    ├── kafka
    ├── analytics
    ├── budget
    └── integration
```

### Package Responsibilities

| Package        | Responsibility                              |
| -------------- | ------------------------------------------- |
| controller     | REST APIs                                   |
| service        | Business Logic                              |
| repository     | Database Access                             |
| budget         | Budget Management Module                    |
| categoryBudget | Category-wise Budget Allocation             |
| kafka          | Event-Driven Processing                     |
| analytics      | Redis Analytics Engine                      |
| notification   | Budget Alert Processing                     |
| email          | Email Delivery System                       |
| scheduler      | Weekly & Monthly Report Automation          |
| monitoring     | Metrics & Observability                     |
| security       | Authentication, Rate Limiting & Idempotency |

---

## 🔐 Authentication Flow

1. User registers → Password encrypted using BCrypt
2. User logs in → JWT token generated
3. Client sends JWT in Authorization header
4. JWT filter validates token
5. Security context is populated
6. Protected resources become accessible

---

## 📖 API Documentation

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

# 📌 Sample APIs

### Auth

```http
POST /api/auth/register
POST /api/auth/login
```

### Expenses

```http
POST /api/expenses
POST /api/categories
GET /api/categories
GET /api/expenses?page=0&size=5
GET /api/expenses?category=Food
```

### Dashboard

```http
GET /api/dashboard
```

### Budgets

```http
POST /api/budgets
GET /api/budgets
```

### Category Budgets

```http
POST /api/category-budgets
GET /api/category-budgets
```

---

## 🚀 Running Locally

### Prerequisites

Make sure the following tools are installed:

* Java 17
* Maven 3.9+
* Docker
* Docker Compose
* Git

---

### Clone Repository

```bash
git clone https://github.com/Rakesh-Ariveni/finflow.git
cd finflow
```

---

### Start Infrastructure Services

FinFlow uses Docker Compose to run supporting services.

```bash
docker-compose up -d
```

This will start:

* MySQL
* Redis
* Zookeeper
* Apache Kafka
* Prometheus
* Grafana

---

### Run the Application

```bash
mvn spring-boot:run
```

or

```bash
mvn clean install
java -jar target/*.jar
```

---

### Verify Services

| Service     | URL                   |
| ----------- | --------------------- |
| Application | http://localhost:8080 |
| Prometheus  | http://localhost:9090 |
| Grafana     | http://localhost:3000 |

---

### Default Infrastructure Ports

| Service     | Port |
| ----------- | ---- |
| Spring Boot | 8080 |
| MySQL       | 3306 |
| Redis       | 6379 |
| Kafka       | 9092 |
| Zookeeper   | 2181 |
| Prometheus  | 9090 |
| Grafana     | 3000 |

---

## ☁️ Deployment Architecture

FinFlow is deployed using a containerized cloud-native architecture on AWS.

### Infrastructure Components

```text
AWS EC2
│
├── Docker Compose
│
├── FinFlow Application
├── MySQL
├── Redis
├── Apache Kafka
├── Zookeeper
├── Prometheus
└── Grafana
```

### Deployment Stack

* AWS EC2
* Docker
* Docker Compose
* Docker Hub
* GitHub Actions

### Containerized Services

| Service    | Purpose                 |
| ---------- | ----------------------- |
| FinFlow    | Core Application        |
| MySQL      | Persistent Data Storage |
| Redis      | Analytics & Caching     |
| Kafka      | Event Streaming         |
| Zookeeper  | Kafka Coordination      |
| Prometheus | Metrics Collection      |
| Grafana    | Monitoring Dashboards   |

### Production Features

* Containerized Deployment
* Service Isolation
* Persistent Storage
* Automated Monitoring
* Centralized Metrics Collection
* Cloud Hosting on AWS EC2

---

## 🔄 CI/CD Pipeline

FinFlow uses GitHub Actions to automate build, packaging, containerization, and deployment workflows.

### Continuous Integration (CI)

Every push to the main branch automatically triggers:

```text
Git Push
    │
    ▼
GitHub Actions
    │
    ▼
Checkout Source Code
    │
    ▼
Setup JDK 17
    │
    ▼
Maven Build
    │
    ▼
Run Unit Tests
    │
    ▼
Build Docker Image
    │
    ▼
Push Image to Docker Hub
```

### Continuous Deployment (CD)

After a successful build:

```text
Docker Hub
    │
    ▼
AWS EC2
    │
    ▼
Pull Latest Image
    │
    ▼
Restart Application Container
    │
    ▼
Deploy Updated Version
```

### CI/CD Technologies

* GitHub Actions
* Maven
* Docker
* Docker Hub
* AWS EC2

### Benefits

* Automated Build Validation
* Consistent Deployments
* Faster Release Cycle
* Reduced Manual Errors
* Production-Ready Delivery Workflow

---

## 📈 Monitoring & Observability

FinFlow includes a production-grade observability stack to monitor application health, performance, and usage metrics.

### Monitoring Architecture

```text
Spring Boot Application
          │
          ▼
Spring Actuator
          │
          ▼
Prometheus
          │
          ▼
Grafana Dashboards
```

### Metrics Collected

#### Application Metrics

* API Usage Metrics
* Request Monitoring
* Health Checks
* JVM Metrics

#### Cache Metrics

* Cache Hits
* Cache Misses
* Cache Efficiency Monitoring

#### Analytics Metrics

* Dashboard Usage
* Expense Analytics Processing
* Notification Processing

### Monitoring Stack

* Spring Boot Actuator
* Prometheus
* Grafana

### Benefits

* Real-Time Monitoring
* Faster Issue Detection
* Operational Visibility
* Production Health Tracking

---

## 🎯 Key Engineering Concepts Demonstrated

FinFlow was designed to showcase real-world backend engineering principles beyond traditional CRUD applications.

### Backend Engineering

* RESTful API Design
* Layered Architecture
* DTO-Based Communication
* Global Exception Handling
* Input Validation

### Distributed Systems

* Event-Driven Architecture
* Apache Kafka Messaging
* Multiple Consumer Groups
* Retry Mechanisms
* Dead Letter Queues (DLQ)

### Performance Optimization

* Redis Caching
* Real-Time Analytics Aggregation
* Cache-First Dashboard Strategy
* Distributed Rate Limiting

### Reliability Patterns

* Token Bucket Rate Limiting
* Idempotency Protection
* Alert Deduplication
* Fault Tolerance

### Security

* JWT Authentication
* Spring Security
* Password Encryption
* User-Level Data Isolation

### DevOps Practices

* Docker Containerization
* Docker Compose Orchestration
* CI/CD Automation
* Cloud Deployment

### Observability

* Metrics Collection
* Application Monitoring
* Dashboard Visualization
* Operational Visibility

---

## 🔮 Future Enhancements

Potential improvements and future roadmap for FinFlow.

### Finance Features

* Recurring Expense Management
* Savings Goals Tracking
* Investment Portfolio Tracking
* Multi-Currency Support
* Financial Goal Planning

### Notification Enhancements

* In-App Notification Center
* Push Notifications
* SMS Notifications
* Notification Preferences

### Analytics Enhancements

* AI-Powered Spending Insights
* Expense Forecasting
* Predictive Budget Recommendations
* Personalized Financial Insights

### Platform Enhancements

* Mobile Application
* Multi-Tenant Architecture
* Role-Based Access Control (RBAC)
* OAuth2 / Social Login
* Kubernetes Deployment

---

## 👨‍💻 Author

### Rakesh Ariveni

Backend Engineer passionate about building scalable, event-driven, cloud-native applications using Java, Spring Boot, Kafka, Redis, and AWS.

### Project Highlights

* Built using Java 17 & Spring Boot 3
* Event-Driven Architecture with Apache Kafka
* Redis-Powered Analytics Engine
* Intelligent Budget Management System
* Dockerized AWS Deployment
* GitHub Actions CI/CD Pipeline
* Production Monitoring with Prometheus & Grafana

---

## ⭐ Support

If you found this project useful, consider giving the repository a star.

It helps others discover the project and motivates future improvements.

### Thanks for visiting FinFlow 🚀

