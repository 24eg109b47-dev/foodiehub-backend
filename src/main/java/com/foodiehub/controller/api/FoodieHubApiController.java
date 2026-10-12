package com.foodiehub.controller.api;

import com.foodiehub.dto.UserRegistrationDto;
import com.foodiehub.entity.Category;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.service.CategoryService;
import com.foodiehub.service.MenuItemService;
import com.foodiehub.service.RestaurantService;
import com.foodiehub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FoodieHubApiController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public FoodieHubApiController(RestaurantService restaurantService,
                                  MenuItemService menuItemService,
                                  CategoryService categoryService,
                                  UserService userService,
                                  PasswordEncoder passwordEncoder) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "FoodieHub REST API");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getRestaurants() {
        return ResponseEntity.ok(restaurantService.getApprovedRestaurants());
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            return ResponseEntity.ok(restaurant);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/restaurants/{id}/menu")
    public ResponseEntity<List<MenuItem>> getRestaurantMenu(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getAvailableMenuItemsForRestaurant(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        return userService.findByEmail(email).map(user -> {
            if (passwordEncoder.matches(password, user.getPassword())) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("role", user.getRole().name());
                userData.put("phone", user.getPhone());
                return ResponseEntity.ok(userData);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found")));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
        try {
            User registered = userService.registerUser(dto);
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", registered.getId());
            userData.put("name", registered.getName());
            userData.put("email", registered.getEmail());
            userData.put("role", registered.getRole().name());
            return ResponseEntity.status(HttpStatus.CREATED).body(userData);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }
}
