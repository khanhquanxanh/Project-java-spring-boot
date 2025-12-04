package com.Project1.demo.dto.request;

import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDTO {
	
	private String productName;
	
	
	private GroupProduct group;
	
	
	private Long price;
	
	
	private Brands brand;
	
	
	private String imageUrl;
}
