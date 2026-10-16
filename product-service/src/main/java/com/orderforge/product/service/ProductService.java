package com.orderforge.product.service;

import com.orderforge.product.model.Product;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    private final Map<String, Product> productMap;
    public ProductService() {
        productMap  = new ConcurrentHashMap<>();
        productMap.put("prod-1", Product.builder()
                .id("prod-1")
                .name("Keyboard")
                .unitPrice(new BigDecimal("2000")).build());
        productMap.put("prod-2", Product.builder()
                .id("prod-2")
                .name("Mouse")
                .unitPrice(new BigDecimal("500")).build());
    }

    public Optional<Product> getProductById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }
        return Optional.ofNullable(productMap.get(id));
    }
}
