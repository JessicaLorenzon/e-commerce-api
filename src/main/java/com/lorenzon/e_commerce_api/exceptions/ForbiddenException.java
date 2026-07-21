package com.lorenzon.e_commerce_api.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("User does not have permission to access this order");
    }
}
