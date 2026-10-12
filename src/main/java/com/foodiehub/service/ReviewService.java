package com.foodiehub.service;

import com.foodiehub.dto.ReviewDto;
import com.foodiehub.entity.Review;

import java.util.List;

public interface ReviewService {
    Review submitReview(Long customerId, ReviewDto dto);
    List<Review> getReviewsForRestaurant(Long restaurantId);
    boolean hasOrderBeenReviewed(Long orderId);
    double getAverageRatingForRestaurant(Long restaurantId);
}
