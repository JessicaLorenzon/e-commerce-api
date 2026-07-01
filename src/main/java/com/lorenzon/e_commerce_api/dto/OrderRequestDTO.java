package com.lorenzon.e_commerce_api.dto;

import java.util.List;

public record OrderRequestDTO(List<OrderItemRequestDTO> items) {
}
