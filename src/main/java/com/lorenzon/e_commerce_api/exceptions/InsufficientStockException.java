package com.lorenzon.e_commerce_api.exceptions;

public class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String name) {
                    super("Insufficient stock for product: " + name);
    }
}
