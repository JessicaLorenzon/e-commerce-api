package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.OrderItemResponseDTO;
import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.lorenzon.e_commerce_api.entities.payment.Payment;
import com.lorenzon.e_commerce_api.entities.payment.PaymentStatus;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.exceptions.BusinessException;
import com.lorenzon.e_commerce_api.exceptions.ForbiddenException;
import com.lorenzon.e_commerce_api.exceptions.InsufficientStockException;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.factories.*;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.mappers.OrderMapper;
import com.lorenzon.e_commerce_api.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    private Long existingOrderId;
    private Long nonExistingOrderId;
    private User user;
    private User admin;
    private User anotherUser;
    private Order order;
    private OrderItem orderItem;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private OrderItemResponseDTO itemResponse;
    private OrderResponseDTO responseDTO;
    private Instant moment;

    @BeforeEach
    void setUp() {
        existingOrderId = 1L;
        nonExistingOrderId = 100L;

        user = UserFactory.createUser();
        admin = UserFactory.createAdmin();
        anotherUser = UserFactory.createUser(3L);
        product = ProductFactory.createProduct();
        cart = CartFactory.createCart(user);
        cartItem = CartItemFactory.createCartItem(product, 2);
        cart.addItem(cartItem);
        moment = Instant.parse("2026-08-18T15:00:00Z");
        order = OrderFactory.createOrder(user, moment);
        orderItem = OrderItemFactory.createOrderItem(order, product, 2, product.getPrice());
        order.addItem(orderItem);

        itemResponse = new OrderItemResponseDTO(product.getId(), product.getName(), 2, product.getPrice(), orderItem.getSubTotal());
        responseDTO = new OrderResponseDTO(order.getId(), user.getId(), order.getMoment(), order.getTotal(), order.getStatus(), List.of(itemResponse));
    }

    @Test
    public void findAllShouldReturnAllOrdersWhenUserIsAdmin() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(admin);
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.findAll();

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.getFirst());
        verify(orderRepository).findAll();
        verify(orderRepository, never()).findAllByUser(admin);
    }

    @Test
    public void findAllShouldReturnUserOrdersWhenUserIsNotAdmin() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findAllByUser(user)).thenReturn(List.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.findAll();

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.getFirst());
        verify(orderRepository).findAllByUser(user);
        verify(orderRepository, never()).findAll();
    }

    @Test
    public void findByIdShouldReturnOrderWhenUserIsOwner() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.findById(existingOrderId);

        assertEquals(responseDTO, result);
        verify(orderRepository).findById(existingOrderId);
    }

    @Test
    public void findByIdShouldReturnOrderWhenUserIsAdmin() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(admin);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.findById(existingOrderId);

        assertEquals(responseDTO, result);
        verify(orderRepository).findById(existingOrderId);
    }

    @Test
    public void findByIdShouldThrowForbiddenExceptionWhenUserIsNotOwner() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(anotherUser);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));

        assertThrows(ForbiddenException.class, () -> {
            orderService.findById(existingOrderId);
        });
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.findById(nonExistingOrderId);
        });
    }

    @Test
    public void cancelShouldCancelOrderAndReturnStock() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        product.setStockQuantity(5);
        orderItem.setQuantity(2);

        OrderResponseDTO result = orderService.cancel(existingOrderId);

        assertEquals(responseDTO, result);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        assertEquals(7, product.getStockQuantity());
    }

    @Test
    public void cancelOrderShouldThrowBusinessExceptionWhenOrderIsAlreadyCanceled() {
        order.setStatus(OrderStatus.CANCELED);

        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> {
            orderService.cancel(existingOrderId);
        });
    }

    @Test
    public void cancelOrderShouldThrowBusinessExceptionWhenOrderIsAlreadyPaid() {
        Payment payment = PaymentFactory.createPayment(PaymentStatus.SUCCESS);
        order = OrderFactory.createOrder(user, moment, payment);

        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> {
            orderService.cancel(existingOrderId);
        });
    }

    @Test
    public void cancelShouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        when(authenticatedUserService.getLoggedUser()).thenReturn(user);
        when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.cancel(nonExistingOrderId);
        });
    }

    @Test
    public void buildOrderShouldCreateOrderFromCart() {
        product.setStockQuantity(10);
        cartItem.setQuantity(2);

        Order result = orderService.buildOrder(cart);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(OrderStatus.WAITING_PAYMENT, result.getStatus());

        OrderItem resultItem = result.getItems().getFirst();

        assertEquals(product, resultItem.getProduct());
        assertEquals(2, resultItem.getQuantity());
        assertEquals(product.getPrice(), resultItem.getPrice());
        assertEquals(8, product.getStockQuantity());
    }

    @Test
    public void buildOrderShouldThrowInsufficientStockExceptionWhenStockIsInsufficient() {
        product.setStockQuantity(1);
        cartItem.setQuantity(2);

        assertThrows(InsufficientStockException.class, () -> {
            orderService.buildOrder(cart);
        });
    }
}
