package com.foodiehub.config;

import com.foodiehub.entity.Category;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.enums.RestaurantStatus;
import com.foodiehub.enums.Role;
import com.foodiehub.repository.CategoryRepository;
import com.foodiehub.repository.MenuItemRepository;
import com.foodiehub.repository.RestaurantRepository;
import com.foodiehub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RestaurantRepository restaurantRepository,
                           CategoryRepository categoryRepository,
                           MenuItemRepository menuItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println(">>> Initializing FoodieHub Demo Data...");

            // 1. Seed Users
            User admin = new User("System Admin", "admin@foodiehub.com", passwordEncoder.encode("admin123"), "9876543210", Role.ROLE_ADMIN);
            User owner = new User("Restaurant Owner", "owner@foodiehub.com", passwordEncoder.encode("owner123"), "9876543211", Role.ROLE_RESTAURANT_OWNER);
            User customer = new User("John Customer", "customer@foodiehub.com", passwordEncoder.encode("customer123"), "9876543212", Role.ROLE_CUSTOMER);

            userRepository.save(admin);
            owner = userRepository.save(owner);
            userRepository.save(customer);

            // 2. Seed Categories
            String[] categoryNames = {"Indian", "Chinese", "Biryani", "Pizza", "Burgers", "Desserts", "Beverages"};
            Map<String, Category> categoryMap = new HashMap<>();

            for (String catName : categoryNames) {
                Category cat = new Category(catName, catName + " special delicacies and popular choices.");
                categoryMap.put(catName, categoryRepository.save(cat));
            }

            // 3. Seed Sample Approved Restaurant
            Restaurant restaurant = new Restaurant();
            restaurant.setName("FoodieHub Kitchen");
            restaurant.setDescription("Authentic multi-cuisine dining experience delivering fresh and delicious meals directly to your doorstep.");
            restaurant.setAddress("123 Gourmet Avenue, Food City");
            restaurant.setPhone("040-12345678");
            restaurant.setOpeningTime("10:00 AM");
            restaurant.setClosingTime("11:00 PM");
            restaurant.setOwner(owner);
            restaurant.setStatus(RestaurantStatus.APPROVED);
            restaurant.setApproved(true);
            restaurant = restaurantRepository.save(restaurant);

            // 4. Seed Menu Items
            createMenuItem(restaurant, categoryMap.get("Biryani"), "Chicken Biryani", "Fragrant basmati rice layered with succulent marinated chicken and aromatic spices.", new BigDecimal("280.00"), "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Biryani"), "Veg Biryani", "Aromatic rice dish cooked with fresh garden vegetables and authentic biryani masala.", new BigDecimal("220.00"), "https://images.unsplash.com/photo-1645177628172-a94c1f96e6db?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Indian"), "Paneer Butter Masala", "Cottage cheese cubes simmered in a rich tomato, butter, and cream gravy.", new BigDecimal("250.00"), "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Pizza"), "Margherita Pizza", "Classic Italian pizza topped with rich tomato sauce, fresh mozzarella, and basil leaves.", new BigDecimal("299.00"), "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Burgers"), "Chicken Burger", "Crispy juicy chicken patty with lettuce, cheese, and spicy mayo in a toasted bun.", new BigDecimal("149.00"), "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Burgers"), "French Fries", "Golden crispy potato fries seasoned with sea salt and herbs.", new BigDecimal("99.00"), "https://images.unsplash.com/photo-1576107232684-1279f390859f?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Desserts"), "Chocolate Brownie", "Fudgy warm chocolate brownie topped with chocolate syrup.", new BigDecimal("120.00"), "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop");
            createMenuItem(restaurant, categoryMap.get("Beverages"), "Cold Coffee", "Chilled creamy blended coffee topped with cocoa powder.", new BigDecimal("80.00"), "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=500&auto=format&fit=crop");

            System.out.println(">>> Demo data successfully seeded for FoodieHub!");
        }
    }

    private void createMenuItem(Restaurant restaurant, Category category, String name, String desc, BigDecimal price, String imageUrl) {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setImage(imageUrl);
        item.setAvailable(true);
        menuItemRepository.save(item);
    }
}
