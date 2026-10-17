package com.orderforge.product.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {
    public record ProductError(String code, String message) {}

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProductError> handleNotFound(ProductNotFoundException exception) {
        return ResponseEntity.status(404)
                .body(new ProductError("PROD-001", exception.getMessage()));
    }
}
