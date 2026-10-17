package com.orderforge.order.exception;

public class InvalidOrderException extends OrderApplicationException {
    public InvalidOrderException(String detail) {
        this(detail, null);
    }

    public InvalidOrderException(String detail, Throwable cause) {
        super(OrderError.INVALID_ORDER, detail, null, cause);
    }
}
