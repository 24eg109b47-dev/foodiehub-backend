package com.foodiehub.repository;

import com.foodiehub.entity.Category;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurant(Restaurant restaurant);
    List<MenuItem> findByRestaurantAndAvailableTrue(Restaurant restaurant);
    List<MenuItem> findByRestaurantAndCategoryAndAvailableTrue(Restaurant restaurant, Category category);
    long countByRestaurant(Restaurant restaurant);
    long countByRestaurantAndAvailableTrue(Restaurant restaurant);
}