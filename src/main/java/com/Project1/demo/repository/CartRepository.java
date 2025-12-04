package com.Project1.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project1.demo.model.Cart;
import com.Project1.demo.model.Product;
import com.Project1.demo.model.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	// Lấy cart của 1 user
//    List<Cart> findByUser(User user);

//    // Tìm 1 sản phẩm trong cart của user
//    Optional<Cart> findByUserAndProduct(User user, Product product);
//
//    // Đếm tổng số lượng item trong giỏ
//    @Query("SELECT SUM(c.quantity) FROM Cart c WHERE c.user = :user")
//    Integer countTotalItemsByUser(@Param("user") User user);
//
//    void deleteByUserAndProduct(User user, Product product);
    
    Optional<Cart> findByUserId(Long userId);
}
