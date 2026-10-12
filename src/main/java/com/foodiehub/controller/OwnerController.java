package com.foodiehub.controller;

import com.foodiehub.dto.MenuItemDto;
import com.foodiehub.dto.RestaurantDto;
import com.foodiehub.entity.*;
import com.foodiehub.enums.OrderStatus;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.security.CustomUserDetails;
import com.foodiehub.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public OwnerController(RestaurantService restaurantService,
                           MenuItemService menuItemService,
                           CategoryService categoryService,
                           OrderService orderService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long ownerId = userDetails.getUser().getId();
        Optional<Restaurant> restaurantOpt = restaurantService.getRestaurantByOwner(ownerId);

        if (restaurantOpt.isEmpty()) {
            model.addAttribute("hasRestaurant", false);
            return "owner/dashboard";
        }

        Restaurant restaurant = restaurantOpt.get();
        long totalMenuItems = menuItemService.countMenuItemsForRestaurant(restaurant.getId());
        long availableItems = menuItemService.countAvailableMenuItemsForRestaurant(restaurant.getId());
        long totalOrders = orderService.countOwnerTotalOrders(ownerId);
        long pendingOrders = orderService.countOwnerPendingOrders(ownerId);
        long completedOrders = orderService.countOwnerCompletedOrders(ownerId);

        List<Order> recentOrders = orderService.getOrdersForOwner(ownerId).stream().limit(5).toList();

        model.addAttribute("hasRestaurant", true);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("totalMenuItems", totalMenuItems);
        model.addAttribute("availableItems", availableItems);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("recentOrders", recentOrders);
        return "owner/dashboard";
    }

    @GetMapping("/restaurant")
    public String viewRestaurantForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long ownerId = userDetails.getUser().getId();
        Optional<Restaurant> restaurantOpt = restaurantService.getRestaurantByOwner(ownerId);

        RestaurantDto restaurantDto = new RestaurantDto();
        if (restaurantOpt.isPresent()) {
            Restaurant r = restaurantOpt.get();
            restaurantDto.setId(r.getId());
            restaurantDto.setName(r.getName());
            restaurantDto.setDescription(r.getDescription());
            restaurantDto.setAddress(r.getAddress());
            restaurantDto.setPhone(r.getPhone());
            restaurantDto.setOpeningTime(r.getOpeningTime());
            restaurantDto.setClosingTime(r.getClosingTime());
            model.addAttribute("restaurant", r);
        }

        model.addAttribute("restaurantDto", restaurantDto);
        return "owner/restaurant";
    }

    @PostMapping("/restaurant/save")
    public String saveRestaurant(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @Valid @ModelAttribute("restaurantDto") RestaurantDto restaurantDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "owner/restaurant";
        }
        restaurantService.createOrUpdateRestaurant(userDetails.getUser().getId(), restaurantDto);
        redirectAttributes.addFlashAttribute("successMessage", "Restaurant information saved. Initial submission requires Admin approval.");
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/menu")
    public String listMenuItems(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long ownerId = userDetails.getUser().getId();
        Optional<Restaurant> restaurantOpt = restaurantService.getRestaurantByOwner(ownerId);

        if (restaurantOpt.isEmpty()) {
            model.addAttribute("menuItems", Collections.emptyList());
            model.addAttribute("hasRestaurant", false);
            return "owner/menu-list";
        }

        List<MenuItem> menuItems = menuItemService.getMenuItemsForRestaurant(restaurantOpt.get().getId());
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("hasRestaurant", true);
        return "owner/menu-list";
    }

    @GetMapping("/menu/add")
    public String showAddMenuItemForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long ownerId = userDetails.getUser().getId();
        if (restaurantService.getRestaurantByOwner(ownerId).isEmpty()) {
            return "redirect:/owner/restaurant";
        }
        model.addAttribute("menuItemDto", new MenuItemDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", false);
        return "owner/menu-form";
    }

    @PostMapping("/menu/add")
    public String addMenuItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @Valid @ModelAttribute("menuItemDto") MenuItemDto dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", false);
            return "owner/menu-form";
        }
        menuItemService.addMenuItem(userDetails.getUser().getId(), dto);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully.");
        return "redirect:/owner/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String showEditMenuItemForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable("id") Long id,
                                      Model model) {
        Long ownerId = userDetails.getUser().getId();
        MenuItem item = menuItemService.getMenuItemById(id);

        // Ownership validation
        if (!item.getRestaurant().getOwner().getId().equals(ownerId)) {
            return "redirect:/owner/menu";
        }

        MenuItemDto dto = new MenuItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setImage(item.getImage());
        dto.setAvailable(item.isAvailable());
        if (item.getCategory() != null) {
            dto.setCategoryId(item.getCategory().getId());
        }

        model.addAttribute("menuItemDto", dto);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("isEdit", true);
        return "owner/menu-form";
    }

    @PostMapping("/menu/edit/{id}")
    public String updateMenuItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @PathVariable("id") Long id,
                                 @Valid @ModelAttribute("menuItemDto") MenuItemDto dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("isEdit", true);
            return "owner/menu-form";
        }
        menuItemService.updateMenuItem(userDetails.getUser().getId(), id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item updated successfully.");
        return "redirect:/owner/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenuItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        menuItemService.deleteMenuItem(userDetails.getUser().getId(), id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted.");
        return "redirect:/owner/menu";
    }

    @GetMapping("/orders")
    public String listOrders(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long ownerId = userDetails.getUser().getId();
        if (restaurantService.getRestaurantByOwner(ownerId).isEmpty()) {
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("hasRestaurant", false);
            return "owner/orders";
        }

        List<Order> orders = orderService.getOrdersForOwner(ownerId);
        model.addAttribute("orders", orders);
        model.addAttribute("hasRestaurant", true);
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "owner/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("id") Long orderId,
                                    @RequestParam("status") OrderStatus newStatus,
                                    RedirectAttributes redirectAttributes) {
        Long ownerId = userDetails.getUser().getId();
        try {
            orderService.updateOrderStatusByOwner(orderId, ownerId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " status updated to " + newStatus);
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/orders";
    }
}
