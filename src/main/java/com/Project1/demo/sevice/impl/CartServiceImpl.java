package com.Project1.demo.sevice.impl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Project1.demo.dto.request.CartItemDTO;
import com.Project1.demo.dto.response.CartDTO;
import com.Project1.demo.model.Cart;
import com.Project1.demo.model.CartItem;
import com.Project1.demo.model.Product;
import com.Project1.demo.model.User;
import com.Project1.demo.repository.CartItemRepository;
import com.Project1.demo.repository.CartRepository;
import com.Project1.demo.repository.ProductRepository;
import com.Project1.demo.sevice.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
	
	private  final CartRepository cartRepository;
	
	private final ProductRepository productRepository;
	
	private final CartItemRepository cartItemRepository;

	@Override
	public int addToCart(User user, Long productId, int quantity) {
		log.info("User = {}",user.getUsername());
		log.info("productId = {}", productId);
		log.info("so luong = {}", quantity);
		Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check nếu product đã tồn tại trong cart
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);

            cart.getItems().add(newItem);
        }

        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }
	
	@Override
    public List<CartDTO> getCartItems(User user) {
        return cartItemRepository.findByCartUserId(user.getId()).stream()
                .map(item -> new CartDTO(
                        item.getProduct().getId(),
                        item.getProduct().getProductName(),
                        item.getProduct().getPrice(),
                        item.getProduct().getImageUrl(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void updateCartItem(User user, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void removeCartItem(User user, Long productId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }
    
    @Override
    public int getCartItemCount(User user) {
        return cartRepository.findByUserId(user.getId())
                .map(cart -> cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum())
                .orElse(0);
    }

}
