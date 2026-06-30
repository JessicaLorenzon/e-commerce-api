package com.lorenzon.e_commerce_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO(
        @NotBlank
        String fullName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password) {
}
