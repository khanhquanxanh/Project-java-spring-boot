package com.Project1.demo.sevice;

import java.util.List;

import com.Project1.demo.dto.request.ProductRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ProductDetailResponse;
import com.Project1.demo.model.Product;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

public interface ProductService {
	
	long saveAddProduct (ProductRequestDTO request);
	
	PageResponse getAllProduct(int pageNo, int pageSize, String... sort);
	
	PageResponse getFilterProductByPrice(int pageNo, int pageSize,Long minPrice, Long maxPrice, String... sort);
	
	PageResponse getProductByBrand(int pageNo, int pageSize,Brands brand, String... sort);	
	
	PageResponse getProductByGroup(int pageNo, int pageSize,GroupProduct group, String... sort);
	

}
