# 04. Modules Description

## 1. Authentication & Security Module
- User Registration (Customer / Owner) with duplicate email validation.
- Spring Security Login & Logout.
- Role-based authorization (`ROLE_CUSTOMER`, `ROLE_RESTAURANT_OWNER`, `ROLE_ADMIN`).

## 2. Customer Module
- Profile & Address Book CRUD.
- Approved Restaurant & Menu Browsing by Category.
- Cart Management with Single-Restaurant Rule.
- Checkout with Address & Payment Selection (COD / Demo Online).
- Order History, Details, Cancellation (`PENDING`), & Delivered Order Reviews.

## 3. Restaurant Owner Module
- Dashboard Analytics (Real DB Counts).
- Restaurant Profile Setup (Requires Admin Approval).
- Menu CRUD with Strict Ownership Checks.
- Order Management with Sequential State Transitions.

## 4. Admin Module
- Real Database Platform Metrics Dashboard.
- User Status Activation / Deactivation.
- Pending Restaurant Approval / Rejection Workflow.
- Category CRUD with Duplicate Name Check.
- System-Wide Order Monitoring.
