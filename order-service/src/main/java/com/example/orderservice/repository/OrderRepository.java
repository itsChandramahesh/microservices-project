package com.example.orderservice.repository;

import com.example.orderservice.model.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
}
