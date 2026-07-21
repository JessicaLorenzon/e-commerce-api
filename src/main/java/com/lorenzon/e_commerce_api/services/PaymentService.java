package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.payment.PaymentStatus;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional
    public String checkout() throws StripeException {
        User user = authenticatedUserService.getLoggedUser();
        Cart cart = user.getCart();
        Order order = orderService.createOrder(cart);
        Session session = stripeService.createSession(order);
        Payment payment = new Payment();
        payment.setStripeSessionId(session.getId());
        payment.setAmount(session.getAmountTotal());
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        cart.getItems().clear();
        return session.getUrl();
    }

    @Transactional
    public void handleSuccessEvent(String stripeSessionId) {
        Payment payment = paymentRepository.findByStripeSessionId(stripeSessionId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.SUCCESS);
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
    }

    @Transactional
    public void handleFailEvent(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.FAILED);
    }
}
