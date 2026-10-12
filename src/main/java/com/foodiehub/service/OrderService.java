package com.foodiehub.service;

import com.foodiehub.dto.CheckoutDto;
import com.foodiehub.entity.Order;
import com.foodiehub.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long customerId, CheckoutDto dto);
    Order getOrderById(Long orderId);
    Order getCustomerOrderDetails(Long orderId, Long customerId);
    Order getOwnerOrderDetails(Long orderId, Long ownerId);
    List<Order> getOrdersForCustomer(Long customerId);
    List<Order> getOrdersForOwner(Long ownerId);
    List<Order> getAllOrdersForAdmin();
    Order cancelOrder(Long orderId, Long customerId);
    Order updateOrderStatusByOwner(Long orderId, Long ownerId, OrderStatus newStatus);
    
    long countOwnerTotalOrders(Long ownerId);
    long countOwnerPendingOrders(Long ownerId);
    long countOwnerCompletedOrders(Long ownerId);
    
    long countAdminTotalOrders();
    long countAdminDeliveredOrders();
    long countAdminCancelledOrders();
}
