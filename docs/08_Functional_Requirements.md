# 08. Functional Requirements

1. **FR1: Registration & Login**: Users must register with name, valid email, phone, and role. Password must be encrypted via BCrypt. Inactive users must be barred from logging in.
2. **FR2: Single Restaurant Cart**: Adding items from multiple restaurants into a single cart must be blocked with an explicit validation message.
3. **FR3: Backend Price Calculation**: Order total must be computed on the server side using the price at order time.
4. **FR4: Order Cancellation**: Order cancellation is allowed only when order status is `PENDING`.
5. **FR5: Sequential Order Status Workflow**: Restaurant owners can transition order status only in sequence: `PENDING` -> `CONFIRMED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`.
6. **FR6: Review & Rating**: Reviews are permitted only on `DELIVERED` orders, with duplicate reviews blocked per order.
7. **FR7: Owner Authorization**: Owners can edit/delete menu items and view orders belonging strictly to their own restaurant.
