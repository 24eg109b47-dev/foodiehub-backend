package com.foodiehub.controller;

import com.foodiehub.entity.*;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public AdminController(UserService userService,
                           RestaurantService restaurantService,
                           CategoryService categoryService,
                           OrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsers = userService.countTotalUsers();
        long totalCustomers = userService.countCustomers();
        long totalOwners = userService.countOwners();
        long totalRestaurants = restaurantService.countTotalRestaurants();
        long pendingApprovals = restaurantService.countPendingApprovals();
        long totalOrders = orderService.countAdminTotalOrders();
        long deliveredOrders = orderService.countAdminDeliveredOrders();
        long cancelledOrders = orderService.countAdminCancelledOrders();

        List<Restaurant> pendingRestaurants = restaurantService.getPendingRestaurantsForAdmin();
        List<Order> recentOrders = orderService.getAllOrdersForAdmin().stream().limit(5).toList();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalOwners", totalOwners);
        model.addAttribute("totalRestaurants", totalRestaurants);
        model.addAttribute("pendingApprovals", pendingApprovals);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("pendingRestaurants", pendingRestaurants);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        User user = userService.toggleUserStatus(userId);
        String statusStr = user.isEnabled() ? "activated" : "deactivated";
        redirectAttributes.addFlashAttribute("successMessage", "User " + user.getEmail() + " " + statusStr + ".");
        return "redirect:/admin/users";
    }

    @GetMapping("/restaurants")
    public String listRestaurants(Model model) {
        List<Restaurant> restaurants = restaurantService.getAllRestaurantsForAdmin();
        model.addAttribute("restaurants", restaurants);
        return "admin/restaurants";
    }

    @PostMapping("/restaurants/{id}/approve")
    public String approveRestaurant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Restaurant restaurant = restaurantService.approveRestaurant(id);
        redirectAttributes.addFlashAttribute("successMessage", "Restaurant '" + restaurant.getName() + "' approved.");
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/restaurants/{id}/reject")
    public String rejectRestaurant(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Restaurant restaurant = restaurantService.rejectRestaurant(id);
        redirectAttributes.addFlashAttribute("successMessage", "Restaurant '" + restaurant.getName() + "' rejected.");
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam("name") String name,
                              @RequestParam(value = "description", required = false) String description,
                              RedirectAttributes redirectAttributes) {
        try {
            categoryService.addCategory(name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Category '" + name + "' added successfully.");
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Long id,
                               @RequestParam("name") String name,
                               @RequestParam(value = "description", required = false) String description,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete category as it may be associated with menu items.");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrdersForAdmin();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
}
