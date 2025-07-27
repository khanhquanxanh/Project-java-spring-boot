package com.Project1.demo.sevice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.Project1.demo.dto.request.ProductRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ProductDetailResponse;
import com.Project1.demo.model.Product;
import com.Project1.demo.repository.ProductRepository;
import com.Project1.demo.sevice.ProductService;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;

	@Override
	public long saveAddProduct(ProductRequestDTO request) {


		Product product = Product.builder()
				.productName(request.getProductName())
				.group(request.getGroup())
				.price(request.getPrice())
				.brand(request.getBrand())
				.imageUrl(request.getImageUrl())
				.build();
		productRepository.save(product);
		return product.getId();
	}
	
	@Override
    public PageResponse getAllProduct(int pageNo, int pageSize, String... sorts) {

        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));

        Page<Product> products = productRepository.findAll(pageable);
        List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	@Override
	public PageResponse getFilterProductByPrice(int pageNo, int pageSize, Long minPrice, Long maxPrice,
			String... sorts) {
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
        
        Page<Product> products;

        if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	
	@Override
	public PageResponse getProductByBrand(int pageNo, int pageSize, Brands brand, String... sorts) {
		
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
		
		Page<Product> products = productRepository.findByBrand(brand, pageable);
		
		List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

	@Override
	public PageResponse getProductByGroup(int pageNo, int pageSize, GroupProduct group, String... sorts) {
		List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sortBy : sorts) {
                log.info("sortBy: {}", sortBy);
                // firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
		
		Page<Product> products = productRepository.findByGroup(group, pageable);
		
		List<ProductDetailResponse> response = products.stream().map(product -> ProductDetailResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .price(product.getPrice())
                .group(product.getGroup())
                .imageUrl(product.getImageUrl())
                .build()).toList();
                
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(response)
                .build();
    }

}
