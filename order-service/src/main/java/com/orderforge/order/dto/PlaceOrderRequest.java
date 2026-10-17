package com.orderforge.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Getter
public class PlaceOrderRequest{
    private String userId;
    private List<OrderLineRequest> orderLines;
}
