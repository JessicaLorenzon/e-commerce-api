package com.lorenzon.e_commerce_api.dto;

import java.math.BigDecimal;

public record CartItemResponseDTO(Long productId, String productName, Integer quantity, BigDecimal subTotal) {
}
