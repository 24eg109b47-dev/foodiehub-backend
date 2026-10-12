package com.foodiehub.repository;

import com.foodiehub.entity.Order;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);
    Optional<Review> findByOrder(Order order);
    boolean existsByOrder(Order order);
}