package com.foodiehub.repository;

import com.foodiehub.entity.Order;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByOrderDateDesc(User customer);
    List<Order> findByRestaurantOrderByOrderDateDesc(Restaurant restaurant);
    List<Order> findAllByOrderByOrderDateDesc();
    
    long countByRestaurant(Restaurant restaurant);
    long countByRestaurantAndStatus(Restaurant restaurant, OrderStatus status);
    
    long countByStatus(OrderStatus status);
}