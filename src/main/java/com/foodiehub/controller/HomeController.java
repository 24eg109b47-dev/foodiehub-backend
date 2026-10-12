package com.foodiehub.controller;

import com.foodiehub.dto.UserRegistrationDto;
import com.foodiehub.entity.User;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.security.CustomUserDetails;
import com.foodiehub.service.RestaurantService;
import com.foodiehub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UserService userService;
    private final RestaurantService restaurantService;

    public HomeController(UserService userService, RestaurantService restaurantService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            return redirectUserByRole(userDetails.getUser());
        }
        model.addAttribute("featuredRestaurants", restaurantService.getApprovedRestaurants());
        return "home";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return redirectUserByRole(userDetails.getUser());
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            return redirectUserByRole(userDetails.getUser());
        }
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto dto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (BusinessRuleViolationException ex) {
            bindingResult.rejectValue("email", "error.userRegistrationDto", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return redirectUserByRole(userDetails.getUser());
    }

    private String redirectUserByRole(User user) {
        switch (user.getRole()) {
            case ROLE_ADMIN:
                return "redirect:/admin/dashboard";
            case ROLE_RESTAURANT_OWNER:
                return "redirect:/owner/dashboard";
            case ROLE_CUSTOMER:
            default:
                return "redirect:/customer/dashboard";
        }
    }
}
