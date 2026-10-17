package com.orderforge.order.exception;

public class ProductServiceException extends OrderApplicationException {
    public ProductServiceException(String detail) {
        this(detail, null);
    }

    public ProductServiceException(String detail, Throwable cause) {
        super(OrderError.INVALID_PRODUCT_RESPONSE, detail, null, cause);
    }
}
