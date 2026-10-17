package com.orderforge.order.service;

import com.orderforge.order.client.ProductClient;
import com.orderforge.order.dto.OrderLineRequest;
import com.orderforge.order.dto.PlaceOrderRequest;
import com.orderforge.order.dto.ProductResponse;
import com.orderforge.order.exception.InvalidOrderException;
import com.orderforge.order.exception.OrderNotFoundException;
import com.orderforge.order.model.Order;
import com.orderforge.order.model.OrderLineItem;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final ProductClient productClient;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public OrderService(ProductClient productClient) {
        this.productClient = productClient;
    }

    private void validatePlaceOrderRequest(PlaceOrderRequest placeOrderRequest) {
        if (placeOrderRequest == null) {
            throw new InvalidOrderException("null placeOrderRequest");
        }
        if (StringUtils.isBlank(placeOrderRequest.getUserId())) {
            throw new InvalidOrderException("empty userId");
        }

        if (CollectionUtils.isEmpty(placeOrderRequest.getOrderLines())) {
            throw new InvalidOrderException("empty orderLines");
        }

        for (OrderLineRequest orderLineRequest : placeOrderRequest.getOrderLines()) {
            if (orderLineRequest == null) {
                throw new InvalidOrderException("empty orderLineRequest");
            }

            if (StringUtils.isBlank(orderLineRequest.getProductId())) {
                throw new InvalidOrderException("empty productId");
            }

            if (orderLineRequest.getQuantity() == null || orderLineRequest.getQuantity() <= 0) {
                throw new InvalidOrderException("Invalid quantity");
            }
        }


    }

    public Order getOrderById(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new InvalidOrderException("Order ID must not be blank.");
        }
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }

    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
        //validate request
        validatePlaceOrderRequest(placeOrderRequest);

        List<OrderLineRequest> orderLines = placeOrderRequest.getOrderLines();
        List<OrderLineItem> orderLineItemList = new ArrayList<>();

        BigDecimal totalSum = BigDecimal.ZERO;
        for (OrderLineRequest orderLineRequest : orderLines) {
            //External Request
            ProductResponse productById = productClient.getProductById(orderLineRequest.getProductId());
            BigDecimal itemSum = productById.unitPrice().multiply(BigDecimal.valueOf(orderLineRequest.getQuantity()));
            totalSum = totalSum.add(itemSum);
            OrderLineItem orderLineItem = OrderLineItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(productById.id())
                    .productName(productById.name())
                    .totalAmount(itemSum)
                    .unitPrice(productById.unitPrice())
                    .quantity(orderLineRequest.getQuantity())
                    .build();
            orderLineItemList.add(orderLineItem);
        }
        Instant now = Instant.now();
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(placeOrderRequest.getUserId())
                .orderLineItems(List.copyOf(orderLineItemList))
                .totalAmount(totalSum)
                .createdAt(now)
                .updatedAt(now)
                .build();
        orders.put(order.getId(), order);
        return order;
    }
}
