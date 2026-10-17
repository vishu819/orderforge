package com.orderforge.order.dto;

public record ApiError(String code, String message, String detailedMessage,
                       String downstreamCode, String traceId) {}
