package com.lorenzon.e_commerce_api.controllers.handlers;

import com.lorenzon.e_commerce_api.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail resourceNotFoundException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/resource-not-found"));
        return problemDetail;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail insufficientStockException(InsufficientStockException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Insufficient stock");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/insufficient-stock"));
        return problemDetail;
    }

    @ExceptionHandler(AlreadyCanceledException.class)
    public ProblemDetail alreadyCanceledException(AlreadyCanceledException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Already canceled");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/business-exception"));
        return problemDetail;
    }

    @ExceptionHandler(UserForbiddenException.class)
    public ProblemDetail userForbiddenException(UserForbiddenException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Forbidden");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/user-forbidden"));
        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail userAlreadyExistsException(UserAlreadyExistsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("User conflict");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/user-conflict"));
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("Field(s) with invalid value(s)");
        problemDetail.setType(URI.create("https://e-commerce-api.com/errors/invalid-fields"));
        return problemDetail;
    }
}
