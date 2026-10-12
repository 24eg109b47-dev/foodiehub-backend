# 06. Database Design

The relational database `foodiehub` consists of 11 core tables generated via JPA/Hibernate:
1. `users`: Stores user identity, BCrypt password, phone, role, and active status.
2. `restaurant`: Stores restaurant details, owner reference, and approval status.
3. `categories`: Stores food menu categories.
4. `menu_item`: Stores individual food items, pricing, availability, and category reference.
5. `address`: Stores customer delivery addresses.
6. `carts`: Stores active shopping cart per user.
7. `cart_items`: Maps menu items to carts with quantities.
8. `orders`: Stores placed orders, status, delivery address, customer, restaurant, and total.
9. `order_items`: Maps purchased items to orders with historical frozen price.
10. `review`: Stores customer ratings (1-5) and comments for delivered orders.
11. `payment`: Stores order payment method, payment status, and transaction reference.
