package com.foodiehub.service;

import com.foodiehub.dto.MenuItemDto;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;

import java.util.List;

public interface MenuItemService {
    MenuItem addMenuItem(Long ownerId, MenuItemDto dto);
    MenuItem updateMenuItem(Long ownerId, Long menuItemId, MenuItemDto dto);
    void deleteMenuItem(Long ownerId, Long menuItemId);
    MenuItem getMenuItemById(Long menuItemId);
    List<MenuItem> getMenuItemsForRestaurant(Long restaurantId);
    List<MenuItem> getAvailableMenuItemsForRestaurant(Long restaurantId);
    List<MenuItem> getAvailableMenuItemsForCategory(Long restaurantId, Long categoryId);
    
    long countMenuItemsForRestaurant(Long restaurantId);
    long countAvailableMenuItemsForRestaurant(Long restaurantId);
}
