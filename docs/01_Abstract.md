# 01. Abstract

## Project Title
**FoodieHub – Online Food Ordering & Restaurant Management System**

## Abstract
FoodieHub is a comprehensive, database-driven full-stack web application designed for online food ordering and restaurant management. Developed as an individual B.Tech academic project, FoodieHub provides an end-to-end digital solution connecting customers, restaurant owners, and system administrators into a single unified platform.

The application addresses modern food delivery logistics by allowing customers to browse approved restaurant menus, manage cart contents with strict single-restaurant constraints, place orders via Cash on Delivery or simulated Online Payment gateways, and track order progress in real-time. For restaurant owners, FoodieHub offers an intuitive dashboard to manage menu items, update operational details, and control order processing through a strict status state machine (`PENDING` -> `CONFIRMED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`). Administrators maintain system integrity by evaluating restaurant registration requests, managing food categories, and toggling user accounts.

Built using Java 17+, Spring Boot, Spring Security, Spring Data JPA, Hibernate, MySQL 8+, Thymeleaf, and Bootstrap 5, FoodieHub enforces strict role-based access control, relational database consistency, and robust backend business logic recalculations.
