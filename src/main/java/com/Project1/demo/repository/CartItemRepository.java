package com.Project1.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Project1.demo.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	
	List<CartItem> findByCartUserId(Long userId);
	
	Optional<CartItem> findByCartUserIdAndProductId(Long userId, Long productId);
	
}
