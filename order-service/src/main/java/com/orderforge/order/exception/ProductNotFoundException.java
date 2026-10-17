package com.orderforge.order.exception;

public class ProductNotFoundException extends OrderApplicationException {
    public ProductNotFoundException(String detail) {
        this(detail, null);
    }

    public ProductNotFoundException(String detail, Throwable cause) {
        super(OrderError.PRODUCT_NOT_FOUND, detail, null, cause);
    }
}
