package com.orderforge.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;


@Builder
@Jacksonized
@Getter
public class OrderLineRequest {
    private String productId;
    private Integer quantity;
}
