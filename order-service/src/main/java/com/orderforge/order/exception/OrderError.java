package com.orderforge.order.exception;

import org.springframework.http.HttpStatus;

public enum OrderError {
    INVALID_ORDER("ORDR-001", "Invalid order request.", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("ORDR-002", "A requested product was not found.", HttpStatus.UNPROCESSABLE_ENTITY),
    PRODUCT_UNAVAILABLE("ORDR-003", "Unable to retrieve product information.", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_PRODUCT_RESPONSE("ORDR-004", "Product Service returned an invalid response.", HttpStatus.BAD_GATEWAY),
    INTERNAL_ERROR("ORDR-005", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    OrderError(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String code() { return code; }
    public String message() { return message; }
    public HttpStatus status() { return status; }
}
