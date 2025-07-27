package com.Project1.demo.dto.request;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private int quantity;
}