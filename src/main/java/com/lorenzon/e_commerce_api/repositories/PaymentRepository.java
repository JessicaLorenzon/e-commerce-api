package com.lorenzon.e_commerce_api.repositories;

import com.lorenzon.e_commerce_api.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripeSessionId(String stripeSessionId);

    Optional<Payment> findByOrderId(Long orderId);
}
