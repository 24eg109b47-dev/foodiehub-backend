# 10. Testing Methodology & Verification

## Automated Testing
- Unit & Integration testing executed via Maven (`mvn clean test`).
- Controller mapping tests, repository query method tests, and spring context loading verification.

## Manual Test Cases Executed
1. **Customer Flow**:
   - Register new customer account -> Login -> Browse restaurants -> Filter menu by category -> Add item -> Attempt adding item from second restaurant (verifying single-restaurant warning) -> Checkout with default address & `DEMO_ONLINE` payment -> Confirm order placement and `DEMO-XXXXXXXX` transaction reference -> Cancel order attempt after status update.
2. **Owner Flow**:
   - Login as owner -> View dashboard metrics -> Create restaurant profile -> Add/Edit/Delete menu item -> Advance order status through `PENDING` -> `CONFIRMED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`.
3. **Admin Flow**:
   - Login as admin -> View system dashboard -> Approve pending restaurant -> Deactivate/Activate user account -> Manage food categories.
