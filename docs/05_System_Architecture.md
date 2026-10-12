# 05. System Architecture

FoodieHub follows a standard 3-Tier Layered Spring Boot Monolithic Architecture:

```
[ Presentation Layer ]  --> Thymeleaf Templates + Bootstrap 5 + Controllers
           │
           ▼
[  Business Layer    ]  --> Service Interfaces & Implementations + Security
           │
           ▼
[  Data Access Layer ]  --> Spring Data JPA Repositories + Hibernate ORM
           │
           ▼
[   Database Layer   ]  --> MySQL 8+ Database (foodiehub)
```
