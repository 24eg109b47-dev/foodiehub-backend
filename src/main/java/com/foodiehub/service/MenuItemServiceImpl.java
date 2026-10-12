package com.foodiehub.service;

import com.foodiehub.dto.MenuItemDto;
import com.foodiehub.entity.Category;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.exception.UnauthorizedAccessException;
import com.foodiehub.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               RestaurantService restaurantService,
                               CategoryService categoryService) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
    }

    @Override
    public MenuItem addMenuItem(Long ownerId, MenuItemDto dto) {
        Restaurant restaurant = restaurantService.getRestaurantByOwner(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant found for owner id: " + ownerId));

        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImage(dto.getImage());
        item.setAvailable(dto.isAvailable());

        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            item.setCategory(category);
        }

        return menuItemRepository.save(item);
    }

    @Override
    public MenuItem updateMenuItem(Long ownerId, Long menuItemId, MenuItemDto dto) {
        MenuItem item = getMenuItemById(menuItemId);
        validateOwnership(item, ownerId);

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setImage(dto.getImage());
        item.setAvailable(dto.isAvailable());

        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            item.setCategory(category);
        } else {
            item.setCategory(null);
        }

        return menuItemRepository.save(item);
    }

    @Override
    public void deleteMenuItem(Long ownerId, Long menuItemId) {
        MenuItem item = getMenuItemById(menuItemId);
        validateOwnership(item, ownerId);
        menuItemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurant(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getAvailableMenuItemsForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return menuItemRepository.findByRestaurantAndAvailableTrue(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getAvailableMenuItemsForCategory(Long restaurantId, Long categoryId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        Category category = categoryService.getCategoryById(categoryId);
        return menuItemRepository.findByRestaurantAndCategoryAndAvailableTrue(restaurant, category);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMenuItemsForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return menuItemRepository.countByRestaurant(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableMenuItemsForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return menuItemRepository.countByRestaurantAndAvailableTrue(restaurant);
    }

    private void validateOwnership(MenuItem item, Long ownerId) {
        if (!item.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to modify this menu item.");
        }
    }
}
