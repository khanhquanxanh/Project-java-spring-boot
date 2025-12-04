package com.Project1.demo.controller.request;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;
    private int quantity;
}
