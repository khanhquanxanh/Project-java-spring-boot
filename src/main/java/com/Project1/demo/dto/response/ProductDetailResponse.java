package com.Project1.demo.dto.response;

import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductDetailResponse {
	
	private Long id;
	
	private String productName;
	
	
	private GroupProduct group;
	
	
	private long price;
	
	
	private Brands brand;
	
	
	private String imageUrl;

}
