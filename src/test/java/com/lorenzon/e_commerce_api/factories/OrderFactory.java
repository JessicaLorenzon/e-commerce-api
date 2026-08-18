package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.user.User;

import java.time.Instant;

public class OrderFactory {

    public static Order createOrder(User user, Instant moment) {
        Order order = new Order();
        order.setId(1L);
        order.setMoment(moment);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setUser(user);
        return order;
    }

    public static Order createOrder(User user, Instant moment, Payment payment) {
        Order order = createOrder(user, moment);
        order.setPayment(payment);
        return order;
    }
}
