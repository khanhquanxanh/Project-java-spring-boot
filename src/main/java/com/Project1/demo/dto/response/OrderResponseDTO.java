package com.Project1.demo.dto.response;

import com.Project1.demo.util.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private long totalAmount;
    private OrderStatus status;
}