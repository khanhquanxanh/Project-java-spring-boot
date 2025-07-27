package com.Project1.demo.sevice;

import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.dto.response.OrderResponseDTO;

public interface OrderService {
	OrderResponseDTO createOrder(OrderRequestDTO request, String token);
}
