# 09. Non-Functional Requirements

1. **Security**: Passwords encrypted using BCrypt. URL access controlled at backend via Spring Security filters.
2. **Performance**: Server-side rendering using Thymeleaf ensures quick response times without heavy client-side JavaScript bundle execution.
3. **Data Integrity**: Atomic database transactions via `@Transactional` ensure orders and payments are created consistently.
4. **Usability**: Clean responsive Bootstrap 5 interface usable on desktop, tablet, and mobile browsers.
