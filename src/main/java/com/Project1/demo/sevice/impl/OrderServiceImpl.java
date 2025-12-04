package com.Project1.demo.sevice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.dto.response.OrderResponseDTO;
import com.Project1.demo.model.Cart;
import com.Project1.demo.model.CartItem;
import com.Project1.demo.model.Order;
import com.Project1.demo.model.OrderItem;
import com.Project1.demo.model.User;
import com.Project1.demo.repository.CartItemRepository;
import com.Project1.demo.repository.CartRepository;
import com.Project1.demo.repository.OrderRepository;
import com.Project1.demo.sevice.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
	
	private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

	@Override
	public Order createOrder(User user, OrderRequestDTO request) {
		// Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCartUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.getDeliveryAddress());

        long totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());

            orderItems.add(item);

            totalAmount += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Lưu đơn hàng (tự cascade items)
        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng sau khi đặt
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    
	}

}
