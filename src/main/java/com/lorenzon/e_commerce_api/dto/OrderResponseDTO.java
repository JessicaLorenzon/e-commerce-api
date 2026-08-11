package com.lorenzon.e_commerce_api.dto;

import com.lorenzon.e_commerce_api.entities.order.OrderStatus;
import com.lorenzon.e_commerce_api.entities.user.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDTO(Long id, Long userId, Instant moment, BigDecimal total, OrderStatus status,
                               List<OrderItemResponseDTO> items) {
}
