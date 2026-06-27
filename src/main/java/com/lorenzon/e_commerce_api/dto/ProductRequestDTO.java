package com.lorenzon.e_commerce_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        @Positive
        BigDecimal price,
        @NotNull
        @Positive
        Integer stockQuantity) {
}
