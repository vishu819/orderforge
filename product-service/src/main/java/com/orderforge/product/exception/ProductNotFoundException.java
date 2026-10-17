package com.orderforge.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("The requested product does not exist.");
    }
}
