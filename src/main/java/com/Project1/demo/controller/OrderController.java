package com.Project1.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.model.Order;
import com.Project1.demo.model.User;
import com.Project1.demo.sevice.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(
            @AuthenticationPrincipal User user,
            @RequestBody OrderRequestDTO request) {

        Order order = orderService.createOrder(user, request);
        return ResponseEntity.ok(order);
    }

}
