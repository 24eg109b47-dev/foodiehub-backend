# 03. Project Objectives

## Primary Objectives
1. **Develop a Production-Ready Monolithic System**: Build a functional Java Spring Boot application integrated with MySQL database.
2. **Implement Robust Security**: Secure endpoints using Spring Security with BCrypt password hashing and custom role redirection.
3. **Enforce Business Integrity**:
   - Limit active cart items to a single restaurant.
   - Freeze item prices inside `OrderItem` at checkout.
   - Restrict order cancellations to `PENDING` state.
   - Ensure sequential order status transitions.
4. **Provide Real Database Analytics**: Eliminate hard-coded metrics; calculate dashboard analytics directly from MySQL.
