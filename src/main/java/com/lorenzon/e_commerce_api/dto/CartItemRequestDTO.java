package com.lorenzon.e_commerce_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDTO(
        @NotNull
        Long productId,
        @NotNull
        @Positive
        Integer quantity) {
}
