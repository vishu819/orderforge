package com.orderforge.order.exception;

public class OrderApplicationException extends RuntimeException {
    private final OrderError error;
    private final String downstreamCode;

    public OrderApplicationException(OrderError error, String detail, String downstreamCode, Throwable cause) {
        super(detail, cause);
        this.error = error;
        this.downstreamCode = downstreamCode;
    }

    public OrderError error() { return error; }
    public String downstreamCode() { return downstreamCode; }
}
