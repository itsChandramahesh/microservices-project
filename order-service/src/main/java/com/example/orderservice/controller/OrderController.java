package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.service.OrderAggregationService;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderAggregationService orderAggregationService;

    public OrderController(OrderAggregationService orderAggregationService) {
        this.orderAggregationService = orderAggregationService;
    }

    @PostMapping("/order")
    public Map<String, Object> placeOrder(@RequestBody(required = false) OrderRequest orderRequest) {
        return orderAggregationService.placeOrder(orderRequest);
    }
}
