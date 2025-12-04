package com.Project1.demo.sevice;

import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.dto.response.OrderResponseDTO;
import com.Project1.demo.model.Order;
import com.Project1.demo.model.User;

public interface OrderService {
	public Order createOrder(User user, OrderRequestDTO request);
}
