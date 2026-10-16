package com.orderforge.product.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
}
