package com.foodiehub.service;

import com.foodiehub.dto.RestaurantDto;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.enums.RestaurantStatus;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserService userService;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, UserService userService) {
        this.restaurantRepository = restaurantRepository;
        this.userService = userService;
    }

    @Override
    public Restaurant createOrUpdateRestaurant(Long ownerId, RestaurantDto dto) {
        User owner = userService.getUserById(ownerId);
        Optional<Restaurant> existingOpt = restaurantRepository.findByOwner(owner);

        Restaurant restaurant;
        if (existingOpt.isPresent()) {
            restaurant = existingOpt.get();
        } else {
            restaurant = new Restaurant();
            restaurant.setOwner(owner);
            restaurant.setStatus(RestaurantStatus.PENDING);
            restaurant.setApproved(false);
        }

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhone(dto.getPhone());
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());

        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurant> getRestaurantByOwner(Long ownerId) {
        User owner = userService.getUserById(ownerId);
        return restaurantRepository.findByOwner(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getApprovedRestaurants() {
        return restaurantRepository.findByApprovedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> searchApprovedRestaurants(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getApprovedRestaurants();
        }
        return restaurantRepository.findByApprovedTrueAndNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurantsForAdmin() {
        return restaurantRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getPendingRestaurantsForAdmin() {
        return restaurantRepository.findByStatus(RestaurantStatus.PENDING);
    }

    @Override
    public Restaurant approveRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        restaurant.setApproved(true);
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant rejectRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurant.setApproved(false);
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalRestaurants() {
        return restaurantRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingApprovals() {
        return restaurantRepository.countByStatus(RestaurantStatus.PENDING);
    }
}
