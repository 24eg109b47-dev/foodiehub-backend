package com.foodiehub.service;

import com.foodiehub.dto.RestaurantDto;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.enums.RestaurantStatus;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant createOrUpdateRestaurant(Long ownerId, RestaurantDto dto);
    Optional<Restaurant> getRestaurantByOwner(Long ownerId);
    Restaurant getRestaurantById(Long id);
    List<Restaurant> getApprovedRestaurants();
    List<Restaurant> searchApprovedRestaurants(String keyword);
    List<Restaurant> getAllRestaurantsForAdmin();
    List<Restaurant> getPendingRestaurantsForAdmin();
    Restaurant approveRestaurant(Long restaurantId);
    Restaurant rejectRestaurant(Long restaurantId);
    
    long countTotalRestaurants();
    long countPendingApprovals();
}
