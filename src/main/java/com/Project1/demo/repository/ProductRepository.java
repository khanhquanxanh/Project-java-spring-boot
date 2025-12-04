package com.Project1.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Project1.demo.model.Product;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	Page<Product> findByPriceBetween(Long minPrice, Long maxPrice, Pageable pageable);
	
	Page<Product> findByBrand(Brands brand, Pageable pageable);
		
	Page<Product> findByGroup(GroupProduct group, Pageable pageable);
	
	boolean existsBySlug(String slug);
	
    Optional<Product> findBySlug(String slug);
	
}
