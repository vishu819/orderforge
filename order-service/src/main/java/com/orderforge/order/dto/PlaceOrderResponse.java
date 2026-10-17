package com.orderforge.order.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceOrderResponse {
    private String orderId;
}
