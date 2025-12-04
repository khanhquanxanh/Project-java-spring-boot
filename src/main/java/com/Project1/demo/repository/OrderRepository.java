package com.Project1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Project1.demo.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
