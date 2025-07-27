package com.Project1.demo.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private List<OrderItemDTO> items;
}