package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.payment.PaymentStatus;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.mappers.OrderMapper;
import com.lorenzon.e_commerce_api.repositories.PaymentRepository;
import com.lorenzon.e_commerce_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderResponseDTO checkout() {
        Cart cart = findCartByUser();
        Order order = orderService.createOrder(cart);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        cart.getItems().clear();
        return orderMapper.toResponseDTO(order);
    }

    private Cart findCartByUser() {
        User user = getLoggedUser();
        return user.getCart();
    }

    private User getLoggedUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return (User) userRepository.findByEmail(email);
    }

}
