package com.Project1.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project1.demo.configuration.Translator;
import com.Project1.demo.dto.request.ProductRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ResponseData;
import com.Project1.demo.dto.response.ResponseError;
import com.Project1.demo.sevice.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productService;
	
	@PostMapping(value = "/add-product")
	public ResponseData<Long> addProduct(@Valid @RequestBody ProductRequestDTO product) {
		log.info("Request add product = {} {} ", product.getProductName(), product.getBrand());

		try {
			long productId = productService.saveAddProduct(product);
			return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("product.add.success"), productId);
		} catch (Exception e) {
			log.error("errorMessage =  {}", e.getMessage(), e.getCause());
			return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "add product fail");
		}
	}
	
	@GetMapping("/show-product")
	//@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseData<?> getAllProduct(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                  @Min(10) @RequestParam(defaultValue = "12", required = false) int pageSize,
												  @RequestParam(required = false) String... sorts) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> users = productService.getAllProduct(pageNo, pageSize, sorts);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error("ERROR_MESSAGE", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

}
