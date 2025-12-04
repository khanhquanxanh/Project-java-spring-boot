package com.Project1.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long productId;
    private String productName;
    private long productPrice;
    private String productImage;
    private int quantity;
}