package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.cart.Cart;
import com.lorenzon.e_commerce_api.entities.cartItem.CartItem;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.entities.user.UserRole;
import com.lorenzon.e_commerce_api.exceptions.AlreadyCanceledException;
import com.lorenzon.e_commerce_api.exceptions.InsufficientStockException;
import com.lorenzon.e_commerce_api.exceptions.ResourceNotFoundException;
import com.lorenzon.e_commerce_api.exceptions.UserForbiddenException;
import com.lorenzon.e_commerce_api.infra.security.AuthenticatedUserService;
import com.lorenzon.e_commerce_api.mappers.OrderMapper;
import com.lorenzon.e_commerce_api.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper mapper;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        User user = authenticatedUserService.getLoggedUser();
        List<Order> orders = user.getRole() == UserRole.ADMIN
                ? orderRepository.findAll()
                : orderRepository.findAllByUser(user);
        return orders.stream().map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        User user = authenticatedUserService.getLoggedUser();
        Order order = findEntityById(id);
        validateAccess(order, user);
        return order;
    }

    @Transactional
    public Order createOrder(Cart cart) {
        Order order = buildOrder(cart);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderResponseDTO cancel(Long id) {
        User user = authenticatedUserService.getLoggedUser();
        Order order = findEntityById(id);
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new AlreadyCanceledException();
        }
        validateAccess(order, user);
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELED);
        return mapper.toResponseDTO(order);
    }

    public Order buildOrder(Cart cart) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setUser(cart.getUser());
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            updateStock(product, cartItem.getQuantity());
            OrderItem item = new OrderItem(order, product, cartItem.getQuantity(), product.getPrice());
            order.addItem(item);
        }
        return order;
    }

    private Order findEntityById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
    }

    private void updateStock(Product product, Integer quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }

    private void validateAccess(Order order, User user) {
        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isOwner = order.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new UserForbiddenException();
        }
    }
}
