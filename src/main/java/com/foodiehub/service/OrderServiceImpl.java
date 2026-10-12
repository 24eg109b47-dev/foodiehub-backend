package com.foodiehub.service;

import com.foodiehub.dto.CheckoutDto;
import com.foodiehub.entity.*;
import com.foodiehub.enums.OrderStatus;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.exception.UnauthorizedAccessException;
import com.foodiehub.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final RestaurantService restaurantService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserService userService,
                            AddressService addressService,
                            CartService cartService,
                            PaymentService paymentService,
                            RestaurantService restaurantService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.restaurantService = restaurantService;
    }

    @Override
    public Order placeOrder(Long customerId, CheckoutDto dto) {
        User customer = userService.getUserById(customerId);
        Address address = addressService.getAddressById(dto.getAddressId(), customerId);
        Cart cart = cartService.getOrCreateCartForUser(customerId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessRuleViolationException("Cannot place order with an empty cart.");
        }

        // Identify restaurant from first item
        Restaurant restaurant = cart.getItems().get(0).getMenuItem().getRestaurant();

        // Validate menu item availability & compute backend total
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        for (CartItem cartItem : cart.getItems()) {
            MenuItem menuItem = cartItem.getMenuItem();
            if (!menuItem.isAvailable()) {
                throw new BusinessRuleViolationException("Menu item '" + menuItem.getName() + "' is no longer available.");
            }
            // FREEZE PRICE AT ORDER TIME
            BigDecimal itemPrice = menuItem.getPrice();
            BigDecimal itemSubtotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            calculatedTotal = calculatedTotal.add(itemSubtotal);

            OrderItem orderItem = new OrderItem(order, menuItem, cartItem.getQuantity(), itemPrice);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        order.setOrderItems(orderItems);

        // Save order & create payment
        Order savedOrder = orderRepository.save(order);
        Payment payment = paymentService.createPayment(savedOrder, dto.getPaymentMethod());
        savedOrder.setPayment(payment);

        // Clear customer cart
        cartService.clearCart(customerId);

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getCustomerOrderDetails(Long orderId, Long customerId) {
        Order order = getOrderById(orderId);
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedAccessException("You are not authorized to view this order.");
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOwnerOrderDetails(Long orderId, Long ownerId) {
        Order order = getOrderById(orderId);
        if (!order.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this restaurant's order.");
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForCustomer(Long customerId) {
        User customer = userService.getUserById(customerId);
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForOwner(Long ownerId) {
        Restaurant restaurant = restaurantService.getRestaurantByOwner(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant found for owner id: " + ownerId));
        return orderRepository.findByRestaurantOrderByOrderDateDesc(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    public Order cancelOrder(Long orderId, Long customerId) {
        Order order = getCustomerOrderDetails(orderId, customerId);

        // STRICT CANCEL VALIDATION RULE: ONLY PENDING CAN BE CANCELLED
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleViolationException("Order cannot be cancelled once it is " + order.getStatus() + ".");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatusByOwner(Long orderId, Long ownerId, OrderStatus newStatus) {
        Order order = getOwnerOrderDetails(orderId, ownerId);
        OrderStatus currentStatus = order.getStatus();

        // VALIDATE SEQUENTIAL STATUS TRANSITIONS
        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {
            throw new BusinessRuleViolationException("Cannot change status of an order that is already " + currentStatus + ".");
        }

        if (newStatus == OrderStatus.CONFIRMED && currentStatus != OrderStatus.PENDING) {
            throw new BusinessRuleViolationException("Order can only move to CONFIRMED from PENDING state.");
        }
        if (newStatus == OrderStatus.PREPARING && currentStatus != OrderStatus.CONFIRMED) {
            throw new BusinessRuleViolationException("Order can only move to PREPARING from CONFIRMED state.");
        }
        if (newStatus == OrderStatus.OUT_FOR_DELIVERY && currentStatus != OrderStatus.PREPARING) {
            throw new BusinessRuleViolationException("Order can only move to OUT_FOR_DELIVERY from PREPARING state.");
        }
        if (newStatus == OrderStatus.DELIVERED && currentStatus != OrderStatus.OUT_FOR_DELIVERY) {
            throw new BusinessRuleViolationException("Order can only move to DELIVERED from OUT_FOR_DELIVERY state.");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOwnerTotalOrders(Long ownerId) {
        return restaurantService.getRestaurantByOwner(ownerId)
                .map(orderRepository::countByRestaurant)
                .orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOwnerPendingOrders(Long ownerId) {
        return restaurantService.getRestaurantByOwner(ownerId)
                .map(r -> orderRepository.countByRestaurantAndStatus(r, OrderStatus.PENDING))
                .orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOwnerCompletedOrders(Long ownerId) {
        return restaurantService.getRestaurantByOwner(ownerId)
                .map(r -> orderRepository.countByRestaurantAndStatus(r, OrderStatus.DELIVERED))
                .orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAdminTotalOrders() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAdminDeliveredOrders() {
        return orderRepository.countByStatus(OrderStatus.DELIVERED);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAdminCancelledOrders() {
        return orderRepository.countByStatus(OrderStatus.CANCELLED);
    }
}
