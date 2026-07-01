package com.lorenzon.e_commerce_api.dto;

import java.math.BigDecimal;

public record OrderItemResponseDTO(Long productId, String productName, Integer quantity, BigDecimal price,
                                   BigDecimal subTotal) {
}
