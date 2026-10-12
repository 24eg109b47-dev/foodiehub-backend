# 🍽️ FoodieHub – Backend Service & REST API

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.9%2B-red?logo=apachemaven)
![Build](https://img.shields.io/badge/Build-Passing-success)

> **FoodieHub** is an enterprise-grade, database-driven backend application for online food delivery and restaurant management. Developed as a high-performance Java 17 + Spring Boot 3 service with Spring MVC, Spring Data JPA, Hibernate, Spring Security, and MySQL.

---

## 🏗️ Architecture Overview

```text
┌────────────────────────────────────────────────────────┐
│               Client Applications                     │
│    (React / Vite Frontend, Mobile Apps, Thymeleaf UI)  │
└───────────────────────────┬────────────────────────────┘
                            │ REST / HTTP Requests
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Controller Layer                     │
│  - REST API Controllers (/api/**)                      │
│  - Spring MVC View Controllers (Thymeleaf)             │
│  - Global Exception Handler                            │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    Service Layer                       │
│  - User & Authentication Management (BCrypt, RBAC)     │
│  - Restaurant & Menu Lifecycle                         │
│  - Cart & Multi-Item Order State Machine               │
│  - Payment Simulation & Customer Reviews               │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                  Data Access Layer                     │
│  - Spring Data JPA Repositories                        │
│  - Hibernate ORM                                       │
└───────────────────────────┬────────────────────────────┘
                            │ JDBC
                            ▼
┌────────────────────────────────────────────────────────┐
│                    MySQL Database                      │
│  - 11 Relational Tables (Users, Orders, Carts, etc.)   │
└────────────────────────────────────────────────────────┘
```

---

## 🌟 Key Features

### 👤 Customer Features
* **Authentication & Authorization**: Registration, BCrypt password encryption, session-based & REST token authentication.
* **Address Book**: Manage multiple delivery addresses with default selection.
* **Restaurant & Menu Discovery**: Search approved restaurants, view opening/closing hours, filter by categories.
* **Cart Logic**: Enforces single-restaurant ordering constraint.
* **Order Lifecycle & Tracking**: Real-time status updates (`PENDING` ➔ `CONFIRMED` ➔ `PREPARING` ➔ `OUT_FOR_DELIVERY` ➔ `DELIVERED` ➔ `CANCELLED`).
* **Payment Simulation**: Support for Cash on Delivery (COD) and Online Simulation with unique transaction references (`DEMO-XXXXXXXX`).

### 🏪 Restaurant Owner Features
* **Live Dashboard**: Real-time stats on menu items, active orders, and sales metrics.
* **Restaurant Profile**: Update restaurant metadata, timings, and contact details.
* **Menu Item CRUD**: Full management of menu items with pricing and category assignments.
* **Order Processing**: Sequential order status state machine.

### 🛡️ Admin Features
* **Platform Metrics**: Overview of users, restaurants, orders, and revenue.
* **Approvals & Governance**: Review and approve/reject new restaurant registrations.
* **Category Management**: Add, update, and manage system-wide food categories.
* **User Control**: Deactivate or reactivate customer and restaurant owner accounts.

---

## 📡 REST API Documentation

| Method | Endpoint | Description | Auth Required |
|:---|:---|:---|:---|
| `GET` | `/api/health` | Health check endpoint | Public |
| `GET` | `/api/restaurants` | List all approved restaurants | Public |
| `GET` | `/api/restaurants/{id}` | Get restaurant details by ID | Public |
| `GET` | `/api/restaurants/{id}/menu` | Get available menu items for a restaurant | Public |
| `GET` | `/api/categories` | List all food categories | Public |
| `POST` | `/api/auth/login` | Authenticate customer or owner | Public |
| `POST` | `/api/auth/register` | Register a new customer account | Public |

---

## 🗄️ Database Schema & Entities

The backend manages 11 relational entities mapped to MySQL:
* `users` – Customers, Restaurant Owners, Administrators
* `restaurant` – Restaurant details, operational hours, approval status
* `categories` – Food categories (Biryani, Burgers, Desserts, etc.)
* `menu_item` – Food items with price, description, and availability
* `address` – Customer delivery addresses
* `carts` & `cart_items` – Active shopping cart per customer
* `orders` & `order_items` – Customer order history and order lines
* `payment` – Payment transaction records
* `review` – Customer ratings and reviews

---

## 🔑 Demo Accounts (Auto-Seeded)

| Role | Email | Password |
|---|---|---|
| **System Admin** | `admin@foodiehub.com` | `admin123` |
| **Restaurant Owner** | `owner@foodiehub.com` | `owner123` |
| **Customer** | `customer@foodiehub.com` | `customer123` |

---

## 🚀 Quick Start Guide

### Prerequisites
* **Java**: JDK 17 or higher
* **Maven**: 3.8+
* **MySQL**: 8.0+ running on port `3306`

### 1. Database Configuration
Update `src/main/resources/application.properties` if your MySQL credentials differ:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodiehub?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build & Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application runs on: **[http://localhost:8080](http://localhost:8080)**

---

## 📂 Project Structure

```text
foodiehub-backend/
├── pom.xml                     # Maven build configuration
├── README.md                   # Backend documentation
├── docs/                       # B.Tech Academic Documentation
│   ├── 01_Abstract.md
│   ├── 02_Problem_Statement.md
│   ├── 03_Objectives.md
│   ├── 04_Modules.md
│   ├── 05_System_Architecture.md
│   ├── 06_Database_Design.md
│   ├── 07_ER_Diagram.md
│   ├── 08_Functional_Requirements.md
│   ├── 09_Non_Functional_Requirements.md
│   ├── 10_Testing.md
│   ├── 11_Future_Enhancements.md
│   └── 12_Conclusion.md
└── src/
    └── main/
        ├── java/com/foodiehub/
        │   ├── config/         # Security, CORS & DataInitializer
        │   ├── controller/     # MVC & REST API Controllers
        │   ├── dto/            # Data Transfer Objects
        │   ├── entity/         # JPA Entities
        │   ├── enums/          # Status and Role enums
        │   ├── exception/      # Global Exception Handler
        │   ├── repository/     # Spring Data JPA Repositories
        │   ├── security/       # Spring Security & CustomUserDetails
        │   └── service/        # Service interfaces & implementations
        └── resources/
            ├── application.properties
            ├── static/         # CSS & JS assets
            └── templates/      # Thymeleaf HTML views
```

---

## 👨‍💻 Author
* **GitHub**: [@24eg109b47-dev](https://github.com/24eg109b47-dev)
