package com.foodiehub.service;

import com.foodiehub.entity.Order;
import com.foodiehub.entity.Payment;
import com.foodiehub.enums.PaymentMethod;
import com.foodiehub.enums.PaymentStatus;
import com.foodiehub.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createPayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());

        if (paymentMethod == PaymentMethod.DEMO_ONLINE) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            String ref = "DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.setTransactionReference(ref);
        } else {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setTransactionReference("COD-" + System.currentTimeMillis());
        }

        return paymentRepository.save(payment);
    }
}
