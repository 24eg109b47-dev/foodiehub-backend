package com.foodiehub.repository;

import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.enums.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByOwner(User owner);
    List<Restaurant> findByStatus(RestaurantStatus status);
    List<Restaurant> findByApprovedTrue();
    List<Restaurant> findByApprovedTrueAndNameContainingIgnoreCase(String keyword);
    long countByStatus(RestaurantStatus status);
    long countByApprovedTrue();
}