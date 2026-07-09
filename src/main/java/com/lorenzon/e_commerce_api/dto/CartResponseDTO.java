package com.lorenzon.e_commerce_api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(Long id, List<CartItemResponseDTO> items, BigDecimal total) {
}
