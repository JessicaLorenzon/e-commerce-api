package com.lorenzon.e_commerce_api.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
