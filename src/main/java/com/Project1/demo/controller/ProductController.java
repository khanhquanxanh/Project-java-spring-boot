package com.Project1.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Project1.demo.configuration.Translator;
import com.Project1.demo.dto.request.ProductRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ResponseData;
import com.Project1.demo.dto.response.ResponseError;
import com.Project1.demo.model.Product;
import com.Project1.demo.sevice.ProductService;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productService;
	
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseData<?> addProduct(
	        @RequestParam("productName") String name,
	        @RequestParam("group") String group,
	        @RequestParam("brand") String brand,
	        @RequestParam("price") Long price,
	        @RequestParam("imageFile") MultipartFile file
	) {
	    try {

	        ProductRequestDTO dto = new ProductRequestDTO();
	        dto.setProductName(name);
	        dto.setGroup(GroupProduct.fromString(group));
	        dto.setBrand(Brands.fromString(brand));
	        dto.setPrice(price);

	        productService.addProduct(dto, file);

	        return new ResponseData<>(200, "Add success");
	    } catch (Exception e) {
	        return new ResponseData<>(400, e.getMessage());
	    }
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
	    try {
	        Product p = productService.getProductById(id);
	        return ResponseEntity.ok(new ResponseData<>(200, "OK", p));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest()
	                .body(new ResponseData<>(400, e.getMessage(), null));
	    }
	}


	@PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Product updated = productService.updateProduct(id, dto, image);          
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    // ===============================
    //        DELETE PRODUCT
    // ===============================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Delete successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }
	
	@PostMapping(value = "/add-product")
	public ResponseData<Long> addProductfromTomcat(@Valid @RequestBody ProductRequestDTO product) {
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
	@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
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
