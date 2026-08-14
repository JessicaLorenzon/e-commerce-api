package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.dto.CartItemRequestDTO;

public class CartItemRequestFactory {

    public static CartItemRequestDTO createRequest() {
        return new CartItemRequestDTO(1L, 2);
    }
}
