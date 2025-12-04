package com.Project1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Project1.demo.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
