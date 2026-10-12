package com.foodiehub.service;

import com.foodiehub.entity.Order;
import com.foodiehub.entity.Payment;
import com.foodiehub.enums.PaymentMethod;

public interface PaymentService {
    Payment createPayment(Order order, PaymentMethod paymentMethod);
}
