package com.Project1.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project1.demo.dto.request.CartItemDTO;
import com.Project1.demo.dto.response.CartDTO;
import com.Project1.demo.model.Cart;
import com.Project1.demo.model.User;
import com.Project1.demo.sevice.CartService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO request,
                                       @AuthenticationPrincipal User user) {

        int count = cartService.addToCart(user, request.getProductId(), request.getQuantity());

        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "cartCount", count
        ));
    }
    
 // Lấy toàn bộ cart items của user
    @GetMapping("/items")
    public ResponseEntity<List<CartDTO>> getCartItems(@AuthenticationPrincipal User user) {
        List<CartDTO> items = cartService.getCartItems(user);
        return ResponseEntity.ok(items);
    }

    // Update số lượng sản phẩm trong giỏ
    @PostMapping("/update")
    public ResponseEntity<?> updateCart(@AuthenticationPrincipal User user,
                                        @RequestBody CartItemDTO request) {
        cartService.updateCartItem(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    // Xóa sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public ResponseEntity<?> removeCartItem(@AuthenticationPrincipal User user,
                                            @RequestBody CartItemDTO request) {
        cartService.removeCartItem(user, request.getProductId());
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
    
    // Cập nhật số lượng giỏ hàng giỏ
    @GetMapping("/count")
    public ResponseEntity<?> getCartCount(@AuthenticationPrincipal User user) {
        int count = cartService.getCartItemCount(user);
        log.info("count = {}", count);
        return ResponseEntity.ok(Map.of("count", count));
    }
}