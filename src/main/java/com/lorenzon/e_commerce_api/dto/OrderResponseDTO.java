package com.lorenzon.e_commerce_api.dto;

import com.lorenzon.e_commerce_api.entities.order.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponseDTO(Long id, Instant moment, OrderStatus status, List<OrderItemResponseDTO> items) {
}
