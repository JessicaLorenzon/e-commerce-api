package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.payment.PaymentStatus;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.factories.CartFactory;
import com.lorenzon.e_commerce_api.factories.OrderFactory;
import com.lorenzon.e_commerce_api.factories.PaymentFactory;
import com.lorenzon.e_commerce_api.factories.UserFactory;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private StripeService stripeService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    private User user;
    private Cart cart;
    private Order order;
    private Session session;
    private Instant moment;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        cart = CartFactory.createCart(user);
        moment = Instant.parse("2026-08-18T15:00:00Z");
        order = OrderFactory.createOrder(user, moment);

        session = new Session();
        session.setId("cs_test_123");
        session.setAmountTotal(20000L);
        session.setUrl("https://checkout.stripe.com/test");
    }

    @Test
    public void checkoutShouldCreatePaymentAndReturnStripeUrl() throws StripeException {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderService.createOrder(cart)).thenReturn(order);
        when(stripeService.createSession(order)).thenReturn(session);

        String result = paymentService.checkout();

        assertEquals(session.getUrl(), result);
        assertTrue(cart.getItems().isEmpty());
        verify(orderService).createOrder(cart);
        verify(stripeService).createSession(order);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(session.getId(), savedPayment.getStripeSessionId());
        assertEquals(session.getAmountTotal(), savedPayment.getAmount());
        assertEquals(order, savedPayment.getOrder());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
    }

    @Test
    public void handleSuccessEventShouldMarkPaymentAsSuccessAndOrderAsPaid() {
        Payment payment = PaymentFactory.createPayment(PaymentStatus.PENDING);
        payment.setOrder(order);

        when(paymentRepository.findByStripeSessionId(session.getId())).thenReturn(Optional.of(payment));

        paymentService.handleSuccessEvent(session.getId());

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(OrderStatus.PAID, payment.getOrder().getStatus());
        verify(paymentRepository).findByStripeSessionId(session.getId());
    }

    @Test
    public void handleSuccessEventShouldThrowResourceNotFoundExceptionWhenPaymentDoesNotExist() {
        session.setId("invalid-session");

        when(paymentRepository.findByStripeSessionId(session.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.handleSuccessEvent(session.getId());
        });
    }

    @Test
    public void handleFailEventShouldMarkPaymentAsFailed() {
        Payment payment = PaymentFactory.createPayment(PaymentStatus.PENDING);
        payment.setOrder(order);

        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.of(payment));

        paymentService.handleFailEvent(order.getId());

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        verify(paymentRepository).findByOrderId(order.getId());
    }

    @Test
    public void handleFailEventShouldThrowResourceNotFoundExceptionWhenPaymentDoesNotExist() {
        order.setId(100L);

        when(paymentRepository.findByOrderId(order.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.handleFailEvent(order.getId());
        });
    }
}
