package com.lorenzon.e_commerce_api.exceptions;

public class AlreadyCanceledException extends RuntimeException {
    public AlreadyCanceledException() {
        super("Order is already canceled");
    }
}
