package com.Project1.demo.sevice.impl;

import org.springframework.stereotype.Service;

import com.Project1.demo.dto.request.OrderItemDTO;
import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.dto.response.OrderResponseDTO;
import com.Project1.demo.model.Order;
import com.Project1.demo.model.OrderItem;
import com.Project1.demo.model.Product;
import com.Project1.demo.model.User;
import com.Project1.demo.repository.OrderRepository;
import com.Project1.demo.repository.ProductRepository;
import com.Project1.demo.repository.UserRepository;
import com.Project1.demo.sevice.JwtService;
import com.Project1.demo.sevice.OrderService;
import com.Project1.demo.util.OrderStatus;

import lombok.RequiredArgsConstructor;

import static com.Project1.demo.util.TokenType.ACCESS_TOKEN;
import static com.Project1.demo.util.TokenType.REFRESH_TOKEN;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
	
	private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

	@Override
	public OrderResponseDTO createOrder(OrderRequestDTO request, String token) {
        String username = jwtService.extractUsername(token,ACCESS_TOKEN);
        User user = userRepository.findByUsername(username);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);

        long total = 0;
        for (OrderItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId()).orElseThrow();
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            order.addItem(item);
            total += product.getPrice() * itemDTO.getQuantity();
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        return new OrderResponseDTO(saved.getId(), total, saved.getStatus());
    }

}
