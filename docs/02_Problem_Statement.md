# 02. Problem Statement

## Problem Context
Traditional food ordering systems rely heavily on manual phone calls, paper menus, and physical cash collection without central coordination. This leads to several operational inefficiencies:
1. **Inefficient Menu Management**: Physical menus are static, causing customer frustration when items are unavailable or prices change without notice.
2. **Order Miscommunication**: Oral order taking frequently results in wrong order items, incorrect delivery addresses, and untracked cancellations.
3. **Lack of Centralized Supervision**: Unregulated food vendors operate without verification, leading to safety and service quality concerns.
4. **Data Discrepancy**: Price fluctuations over time corrupt past financial reports if order histories recalculate totals dynamically from current item prices instead of freezing order-time historical prices.

## Solution Overview
FoodieHub solves these challenges through a centralized web application featuring:
- Role-based authorization (`CUSTOMER`, `RESTAURANT_OWNER`, `ADMIN`).
- Automated restaurant approval workflows requiring administrative verification before public display.
- Price freezing in order historical records (`OrderItem.price`).
- Strict single-restaurant cart validation and order lifecycle state transitions.
