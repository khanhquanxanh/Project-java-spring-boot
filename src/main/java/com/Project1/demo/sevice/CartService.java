package com.Project1.demo.sevice;

import java.util.List;

import com.Project1.demo.dto.request.CartItemDTO;
import com.Project1.demo.dto.response.CartDTO;
import com.Project1.demo.model.Cart;
import com.Project1.demo.model.User;

public interface CartService {
	
	public int addToCart(User user, Long productId, int quantity);
	
	List<CartDTO> getCartItems(User user);

    void updateCartItem(User user, Long productId, int quantity);

    void removeCartItem(User user, Long productId);
    
    public int getCartItemCount(User user);

}
