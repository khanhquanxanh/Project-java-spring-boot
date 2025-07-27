package com.Project1.demo.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.Project1.demo.util.Gender;
import com.Project1.demo.util.UserStatus;
import com.Project1.demo.util.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "OrderItem")
@Table(name = "tbl_orderItem")
public class OrderItem extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private long priceAtPurchase; // Giá tại thời điểm mua
}