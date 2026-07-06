package com.lorenzon.e_commerce_api.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " with ID " + id + " not found");
    }
}
