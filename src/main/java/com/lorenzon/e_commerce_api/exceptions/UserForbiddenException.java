package com.lorenzon.e_commerce_api.exceptions;

public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException() {
        super("User forbidden");
    }
}
