package com.lorenzon.e_commerce_api.dto;

import java.math.BigDecimal;

public record ProductResponseAdminDTO(Long id, String name, String description, BigDecimal price, Integer stockQuantity) {
}
