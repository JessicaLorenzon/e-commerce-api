package com.lorenzon.e_commerce_api.exceptions;

public class CartOrItemNotFoundException extends RuntimeException {
    public CartOrItemNotFoundException() {
        super("Cart or Item not found");
    }
}
