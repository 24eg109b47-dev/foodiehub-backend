package com.foodiehub.service;

import com.foodiehub.entity.Cart;

import java.math.BigDecimal;

public interface CartService {
    Cart getOrCreateCartForUser(Long userId);
    Cart addItemToCart(Long userId, Long menuItemId, int quantity);
    Cart updateCartItemQuantity(Long userId, Long cartItemId, int quantity);
    Cart removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
    BigDecimal calculateCartTotal(Cart cart);
}
