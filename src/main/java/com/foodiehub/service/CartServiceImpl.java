package com.foodiehub.service;

import com.foodiehub.entity.Cart;
import com.foodiehub.entity.CartItem;
import com.foodiehub.entity.MenuItem;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.User;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.exception.UnauthorizedAccessException;
import com.foodiehub.repository.CartItemRepository;
import com.foodiehub.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final MenuItemService menuItemService;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserService userService,
                           MenuItemService menuItemService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.menuItemService = menuItemService;
    }

    @Override
    public Cart getOrCreateCartForUser(Long userId) {
        User user = userService.getUserById(userId);
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    @Override
    public Cart addItemToCart(Long userId, Long menuItemId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Quantity must be greater than zero.");
        }

        MenuItem menuItem = menuItemService.getMenuItemById(menuItemId);
        if (!menuItem.isAvailable()) {
            throw new BusinessRuleViolationException("Selected item '" + menuItem.getName() + "' is currently unavailable.");
        }

        Cart cart = getOrCreateCartForUser(userId);

        // SINGLE-RESTAURANT RESTRICTION CHECK
        if (!cart.getItems().isEmpty()) {
            Restaurant existingRestaurant = cart.getItems().get(0).getMenuItem().getRestaurant();
            if (!existingRestaurant.getId().equals(menuItem.getRestaurant().getId())) {
                throw new BusinessRuleViolationException("Your cart contains items from another restaurant. Clear your cart before adding this item.");
            }
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndMenuItem(cart, menuItem);
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, menuItem, quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCartForUser(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedAccessException("Unauthorized cart operation.");
        }

        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
        return updateCartItemQuantity(userId, cartItemId, 0);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCartForUser(userId);
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal calculateCartTotal(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
