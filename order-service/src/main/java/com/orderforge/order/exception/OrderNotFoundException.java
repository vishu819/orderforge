package com.orderforge.order.exception;

public class OrderNotFoundException extends OrderApplicationException {
    public OrderNotFoundException() {
        super(OrderError.ORDER_NOT_FOUND, "The requested order does not exist.", null, null);
    }
}
