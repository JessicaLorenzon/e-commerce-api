package com.lorenzon.e_commerce_api.repositories;

import com.lorenzon.e_commerce_api.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
