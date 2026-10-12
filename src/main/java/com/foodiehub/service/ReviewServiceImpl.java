package com.foodiehub.service;

import com.foodiehub.dto.ReviewDto;
import com.foodiehub.entity.Order;
import com.foodiehub.entity.Restaurant;
import com.foodiehub.entity.Review;
import com.foodiehub.entity.User;
import com.foodiehub.enums.OrderStatus;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderService orderService,
                             UserService userService,
                             RestaurantService restaurantService) {
        this.reviewRepository = reviewRepository;
        this.orderService = orderService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @Override
    public Review submitReview(Long customerId, ReviewDto dto) {
        User customer = userService.getUserById(customerId);
        Order order = orderService.getCustomerOrderDetails(dto.getOrderId(), customerId);

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessRuleViolationException("Reviews can only be submitted for DELIVERED orders.");
        }

        if (reviewRepository.existsByOrder(order)) {
            throw new BusinessRuleViolationException("You have already submitted a review for this order.");
        }

        Review review = new Review();
        review.setCustomer(customer);
        review.setRestaurant(order.getRestaurant());
        review.setOrder(order);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOrderBeenReviewed(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return reviewRepository.existsByOrder(order);
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageRatingForRestaurant(Long restaurantId) {
        List<Review> reviews = getReviewsForRestaurant(restaurantId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return Math.round((sum / reviews.size()) * 10.0) / 10.0;
    }
}
