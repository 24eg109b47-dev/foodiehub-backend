# FoodieHub – Online Food Ordering & Restaurant Management System

**FoodieHub** is a complete, production-ready, database-driven full-stack web application designed for online food ordering and restaurant management. This project is built as an individual B.Tech academic project using Java 17+, Spring Boot 3, Spring MVC, Spring Data JPA, Hibernate, Spring Security, MySQL 8+, Thymeleaf, and Bootstrap 5.

---

## 🌟 Key Features

### 👤 Customer Module
- **User Registration & Security**: Secure account creation with email uniqueness checks and BCrypt password encryption.
- **Profile & Address Book**: Manage profile details and multiple delivery addresses (with default selection).
- **Restaurant & Menu Browsing**: Search approved restaurants, view operational hours, filter menu items by category.
- **Single-Restaurant Cart**: Enforces the business rule that a cart can only contain items from ONE restaurant at a time.
- **Checkout & Payment Simulation**: Support for Cash on Delivery (COD) and simulated Online Payment (`DEMO-XXXXXXXX` transaction code generation).
- **Order Lifecycle & Tracking**: Real-time order status tracking (`PENDING`, `CONFIRMED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`).
- **Order Cancellation**: Customer order cancellation permitted only when order status is `PENDING`.
- **Ratings & Reviews**: Submit 1 to 5-star ratings and comments for `DELIVERED` orders (duplicate reviews prevented).

### 🏪 Restaurant Owner Module
- **Real Database Dashboard**: Live statistics showing Total Menu Items, Available Items, Pending Orders, Completed Orders, and Total Orders.
- **Restaurant Management**: Create and update restaurant details (initial submission sets status to `PENDING` requiring Admin approval).
- **Menu Item CRUD**: Create, view, edit, and delete menu items with strict owner authorization validation.
- **Order Processing**: Sequential order status state machine (`PENDING` -> `CONFIRMED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`).

### 🛡️ Admin Module
- **System Dashboard**: Live MySQL stats for Total Users, Customers, Owners, Total Restaurants, Pending Approvals, Total Orders, Delivered Orders, and Cancelled Orders.
- **User Management**: View all users and toggle account status (Active / Deactivated). Deactivated users are blocked from logging in.
- **Restaurant Approvals**: Review pending restaurant registrations and perform Approve or Reject actions.
- **Category Management**: Create, edit, and delete food categories with duplicate name prevention.
- **All Orders View**: Platform-wide order inspection.

---

## 🛠️ Technology Stack

- **Backend Framework**: Java 17+, Spring Boot 3.x, Spring MVC, Spring Security, Spring Data JPA, Hibernate
- **Build Tool**: Apache Maven
- **Database**: MySQL 8+
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5, Bootstrap Icons

---

## 📂 Project Structure

```
FoodieHub/
├── pom.xml
├── README.md
├── docs/                      # B.Tech Academic Documentation
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
│
└── src/
    ├── main/
    │   ├── java/com/foodiehub/
    │   │   ├── FoodieHubApplication.java
    │   │   ├── config/        # Security & DataInitializer configuration
    │   │   ├── controller/    # Home, Customer, Owner, Admin controllers
    │   │   ├── dto/           # Data Transfer Objects & form validation
    │   │   ├── entity/        # JPA Entities (User, Restaurant, Order, etc.)
    │   │   ├── enums/         # Role, OrderStatus, PaymentMethod, etc.
    │   │   ├── exception/     # Custom business exceptions & GlobalHandler
    │   │   ├── repository/    # Spring Data JPA repositories
    │   │   ├── security/      # CustomUserDetailsService & SecurityConfig
    │   │   └── service/       # Service interfaces and implementations
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── static/        # CSS, JS static assets
    │       └── templates/     # Thymeleaf HTML pages (auth, customer, owner, admin, error)
    └── test/
```

---

## 🗄️ Database Setup & Configuration

1. **MySQL Database**: Ensure MySQL 8+ service is running locally on port `3306`.
2. **Database Credentials**:
   `application.properties` default settings:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodiehub?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update
   ```

---

## 🔑 Demo Accounts (Auto-Seeded on First Run)

| Role | Email | Password |
|---|---|---|
| **System Admin** | `admin@foodiehub.com` | `admin123` |
| **Restaurant Owner** | `owner@foodiehub.com` | `owner123` |
| **Customer** | `customer@foodiehub.com` | `customer123` |

---

## 🚀 How to Run the Application

### Method 1: Command Line (Maven)
```bash
# Navigate to project directory
cd backend

# Compile and run tests
mvn clean test

# Package application
mvn package

# Run Spring Boot application
mvn spring-boot:run
```

### Method 2: IntelliJ IDEA
1. Open IntelliJ IDEA -> **File > Open** -> Select `FoodieHub` directory.
2. Ensure Project SDK is set to **Java 17+**.
3. Run `FoodieHubApplication.java`.

Access the application in your web browser:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🧪 Verification & Testing Workflow

1. **Customer Workflow**:
   - Log in as `customer@foodiehub.com` / `customer123`.
   - Browse restaurants -> Open "FoodieHub Kitchen".
   - Add "Chicken Biryani" to cart.
   - Go to cart -> Proceed to checkout -> Select address -> Select DEMO_ONLINE payment.
   - Place order -> Observe status `PENDING` and transaction reference.
2. **Owner Workflow**:
   - Log in as `owner@foodiehub.com` / `owner123`.
   - View dashboard -> Check incoming order -> Advance status: `PENDING` -> `CONFIRMED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`.
3. **Customer Review**:
   - Log in back as customer -> Open order history -> Click order -> Submit 5-star rating & review.
4. **Admin Workflow**:
   - Log in as `admin@foodiehub.com` / `admin123`.
   - Inspect users, toggle status, view pending restaurants, approve/reject, add categories.
