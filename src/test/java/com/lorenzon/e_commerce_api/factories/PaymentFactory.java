package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.payment.PaymentStatus;

public class PaymentFactory {

    public static Payment createPayment(PaymentStatus status) {
        Payment payment = new Payment();
        payment.setStatus(status);
        return payment;
    }
}
