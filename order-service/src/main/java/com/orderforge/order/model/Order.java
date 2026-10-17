package com.orderforge.order.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Getter
public class Order {

    private String id;
    private String userId;
    private List<OrderLineItem> orderLineItems;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;

    //id, userId, List<OrderItem> items, totalAmount, createdAt

}
