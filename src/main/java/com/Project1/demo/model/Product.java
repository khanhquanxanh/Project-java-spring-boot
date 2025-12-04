package com.Project1.demo.model;

import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "product")
@Table(name = "tbl_product")
public class Product extends AbstractEntity<Long>{
	
	public Product(Long id) {
        this.setId(id);
    }
	
	@Column(name = "product_name")
	private String productName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "group_product")
	private GroupProduct group;
	
	@Column(name = "price_product")
	private long price;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "brand_product")
	private Brands brand;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(unique = true)
    private String slug;
}
