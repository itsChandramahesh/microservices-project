package com.example.orderservice.controller;

import com.example.orderservice.service.OrderAggregationService;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderAggregationService orderAggregationService;

    public OrderController(OrderAggregationService orderAggregationService) {
        this.orderAggregationService = orderAggregationService;
    }

    @PostMapping("/order")
    public Map<String, Object> placeOrder() {
        return orderAggregationService.placeOrder();
    }
}
