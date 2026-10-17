package com.orderforge.order.controller;

import com.orderforge.order.dto.PlaceOrderRequest;
import com.orderforge.order.dto.PlaceOrderResponse;
import com.orderforge.order.model.Order;
import com.orderforge.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/orders")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody PlaceOrderRequest placeOrderRequest) {
        Order order = orderService.placeOrder(placeOrderRequest);
        PlaceOrderResponse response = PlaceOrderResponse.builder().orderId(order.getId()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
