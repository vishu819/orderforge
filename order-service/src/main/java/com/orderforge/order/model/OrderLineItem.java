package com.orderforge.order.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OrderLineItem {
    private String id;
    private Integer quantity;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
}
