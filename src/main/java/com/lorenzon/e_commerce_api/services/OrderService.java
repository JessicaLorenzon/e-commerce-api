package com.lorenzon.e_commerce_api.services;

import com.lorenzon.e_commerce_api.dto.OrderItemRequestDTO;
import com.lorenzon.e_commerce_api.dto.OrderRequestDTO;
import com.lorenzon.e_commerce_api.dto.OrderResponseDTO;
import com.lorenzon.e_commerce_api.entities.order.Order;
import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.orderItem.OrderItem;
import com.lorenzon.e_commerce_api.entities.product.Product;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.mappers.OrderMapper;
import com.lorenzon.e_commerce_api.repositories.OrderRepository;
import com.lorenzon.e_commerce_api.repositories.ProductRepository;
import com.lorenzon.e_commerce_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderMapper mapper;

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.getReferenceById(id);
        return mapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO insert(OrderRequestDTO orderRequestDTO) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        User user = (User) userRepository.findByEmail(getLoggedUser());
        order.setClient(user);
        for (OrderItemRequestDTO itemRequestDTO : orderRequestDTO.items()) {
            Product product = productRepository.getReferenceById(itemRequestDTO.productId());
            OrderItem item = new OrderItem(order, product, itemRequestDTO.quantity(), product.getPrice());
            order.addItem(item);
        }
        order = orderRepository.save(order);
        return mapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO update(Long id, OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.getReferenceById(id);
        order.getItems().clear();
        for (OrderItemRequestDTO itemDTO : orderRequestDTO.items()) {
            Product product = productRepository.getReferenceById(itemDTO.productId());
            OrderItem item = new OrderItem(product, itemDTO.quantity(), product.getPrice());
            order.addItem(item);
        }
        return mapper.toResponseDTO(order);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private String getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
