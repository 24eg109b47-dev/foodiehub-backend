package com.foodiehub.controller;

import com.foodiehub.dto.AddressDto;
import com.foodiehub.dto.CheckoutDto;
import com.foodiehub.dto.ReviewDto;
import com.foodiehub.dto.UserProfileDto;
import com.foodiehub.entity.*;
import com.foodiehub.enums.OrderStatus;
import com.foodiehub.enums.PaymentMethod;
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

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final AddressService addressService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final UserService userService;

    public CustomerController(RestaurantService restaurantService,
                              MenuItemService menuItemService,
                              CategoryService categoryService,
                              CartService cartService,
                              AddressService addressService,
                              OrderService orderService,
                              ReviewService reviewService,
                              UserService userService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long customerId = userDetails.getUser().getId();
        List<Order> recentOrders = orderService.getOrdersForCustomer(customerId);
        List<Restaurant> restaurants = restaurantService.getApprovedRestaurants();

        model.addAttribute("recentOrders", recentOrders.stream().limit(5).toList());
        model.addAttribute("restaurants", restaurants.stream().limit(4).toList());
        model.addAttribute("totalOrdersCount", recentOrders.size());
        return "customer/dashboard";
    }

    @GetMapping("/restaurants")
    public String listRestaurants(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Restaurant> restaurants = restaurantService.searchApprovedRestaurants(search);
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("search", search);
        return "customer/restaurants";
    }

    @GetMapping("/restaurants/{id}")
    public String viewRestaurant(@PathVariable("id") Long restaurantId,
                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                 Model model) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        List<Category> categories = categoryService.getAllCategories();
        List<MenuItem> menuItems;

        if (categoryId != null) {
            menuItems = menuItemService.getAvailableMenuItemsForCategory(restaurantId, categoryId);
        } else {
            menuItems = menuItemService.getAvailableMenuItemsForRestaurant(restaurantId);
        }

        List<Review> reviews = reviewService.getReviewsForRestaurant(restaurantId);
        double avgRating = reviewService.getAverageRatingForRestaurant(restaurantId);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("categories", categories);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        return "customer/restaurant-detail";
    }

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long customerId = userDetails.getUser().getId();
        Cart cart = cartService.getOrCreateCartForUser(customerId);
        BigDecimal total = cartService.calculateCartTotal(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "customer/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam("menuItemId") Long menuItemId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();
        try {
            cartService.addItemToCart(customerId, menuItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Item added to cart successfully!");
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/cart";
    }

    @PostMapping("/cart/update")
    public String updateCartQuantity(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @RequestParam("cartItemId") Long cartItemId,
                                     @RequestParam("quantity") int quantity,
                                     RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();
        try {
            cartService.updateCartItemQuantity(customerId, cartItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("cartItemId") Long cartItemId,
                                 RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();
        cartService.removeItemFromCart(customerId, cartItemId);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        return "redirect:/customer/cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long customerId = userDetails.getUser().getId();
        Cart cart = cartService.getOrCreateCartForUser(customerId);

        if (cart.getItems().isEmpty()) {
            return "redirect:/customer/cart";
        }

        List<Address> addresses = addressService.getAddressesForUser(customerId);
        BigDecimal total = cartService.calculateCartTotal(cart);

        CheckoutDto checkoutDto = new CheckoutDto();
        addresses.stream()
                .filter(Address::isDefaultAddress)
                .findFirst()
                .ifPresent(addr -> checkoutDto.setAddressId(addr.getId()));

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("addresses", addresses);
        model.addAttribute("checkoutDto", checkoutDto);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "customer/checkout";
    }

    @PostMapping("/order/place")
    public String placeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("checkoutDto") CheckoutDto checkoutDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();

        if (bindingResult.hasErrors()) {
            Cart cart = cartService.getOrCreateCartForUser(customerId);
            model.addAttribute("cart", cart);
            model.addAttribute("total", cartService.calculateCartTotal(cart));
            model.addAttribute("addresses", addressService.getAddressesForUser(customerId));
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "customer/checkout";
        }

        try {
            Order order = orderService.placeOrder(customerId, checkoutDto);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + order.getId() + " placed successfully!");
            return "redirect:/customer/orders/" + order.getId();
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customer/checkout";
        }
    }

    @GetMapping("/orders")
    public String listOrders(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long customerId = userDetails.getUser().getId();
        List<Order> orders = orderService.getOrdersForCustomer(customerId);
        model.addAttribute("orders", orders);
        return "customer/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @PathVariable("id") Long orderId,
                                   Model model) {
        Long customerId = userDetails.getUser().getId();
        Order order = orderService.getCustomerOrderDetails(orderId, customerId);
        boolean isReviewed = reviewService.hasOrderBeenReviewed(orderId);

        if (!model.containsAttribute("reviewDto")) {
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setOrderId(orderId);
            model.addAttribute("reviewDto", reviewDto);
        }

        model.addAttribute("order", order);
        model.addAttribute("isReviewed", isReviewed);
        return "customer/order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable("id") Long orderId,
                              RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();
        try {
            orderService.cancelOrder(orderId, customerId);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " has been cancelled.");
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/orders/" + orderId;
    }

    @PostMapping("/orders/{id}/review")
    public String submitReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable("id") Long orderId,
                               @Valid @ModelAttribute("reviewDto") ReviewDto reviewDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        Long customerId = userDetails.getUser().getId();
        reviewDto.setOrderId(orderId);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid review rating or comment.");
            return "redirect:/customer/orders/" + orderId;
        }

        try {
            reviewService.submitReview(customerId, reviewDto);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your rating and review!");
        } catch (BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/customer/orders/" + orderId;
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.getUserById(userDetails.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("profileDto", new UserProfileDto(user.getName(), user.getPhone()));
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute("profileDto") UserProfileDto profileDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "customer/profile";
        }
        userService.updateProfile(userDetails.getUser().getId(), profileDto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/customer/profile";
    }

    @GetMapping("/addresses")
    public String listAddresses(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long customerId = userDetails.getUser().getId();
        List<Address> addresses = addressService.getAddressesForUser(customerId);
        if (!model.containsAttribute("addressDto")) {
            model.addAttribute("addressDto", new AddressDto());
        }
        model.addAttribute("addresses", addresses);
        return "customer/addresses";
    }

    @PostMapping("/addresses/add")
    public String addAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("addressDto") AddressDto addressDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all required address fields.");
            return "redirect:/customer/addresses";
        }
        addressService.addAddress(userDetails.getUser().getId(), addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Address added successfully.");
        return "redirect:/customer/addresses";
    }

    @PostMapping("/addresses/delete/{id}")
    public String deleteAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable("id") Long addressId,
                                RedirectAttributes redirectAttributes) {
        addressService.deleteAddress(userDetails.getUser().getId(), addressId);
        redirectAttributes.addFlashAttribute("successMessage", "Address deleted.");
        return "redirect:/customer/addresses";
    }

    @PostMapping("/addresses/default/{id}")
    public String setDefaultAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @PathVariable("id") Long addressId,
                                   RedirectAttributes redirectAttributes) {
        addressService.setDefaultAddress(userDetails.getUser().getId(), addressId);
        redirectAttributes.addFlashAttribute("successMessage", "Default address updated.");
        return "redirect:/customer/addresses";
    }
}
