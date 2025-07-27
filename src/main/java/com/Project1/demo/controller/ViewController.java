package com.Project1.demo.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Project1.demo.configuration.Translator;
import com.Project1.demo.dto.request.AddressDTO;
import com.Project1.demo.dto.request.UserRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ResponseData;
import com.Project1.demo.repository.ProductRepository;
import com.Project1.demo.sevice.ProductService;
import com.Project1.demo.sevice.UserServer;
import com.Project1.demo.util.Brands;
import com.Project1.demo.util.GroupProduct;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
@Slf4j
public class ViewController {

	private final ProductService productService;
	
	private final ProductRepository productRepository;

	private final UserServer userService;
	
	@GetMapping("/home")
	public String Home(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()
			    && !(auth instanceof AnonymousAuthenticationToken)) {
			    // Người dùng đã đăng nhập
			    String username = auth.getName();
			    model.addAttribute("username", username);
			    System.out.println("Đã đăng nhập: " + username);
			} else {
			    // Chưa đăng nhập
			    System.out.println("Chưa đăng nhập");
			}
		return "index";
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	@PreAuthorize("hasAuthority('Manager')")
	public String manager(Model model) {
		model.addAttribute("products", productRepository.findAll());
		return "manager";
	}

	@GetMapping("/shop")
	public String shop(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize,
			Model model) {

		PageResponse<?> pageResponse = productService.getAllProduct(pageNo, pageSize);
		model.addAttribute("products", pageResponse.getItems());
		model.addAttribute("pageNo", pageResponse.getPageNo());
        model.addAttribute("totalPage", pageResponse.getTotalPage());
		return "shop";
	}
	
	@GetMapping("/Filter-By-Price")
	public String FilterByPrice(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize,
			@RequestParam Long minPrice,
			@RequestParam Long maxPrice,
			Model model) {

		PageResponse<?> pageResponse = productService.getFilterProductByPrice(pageNo, pageSize, minPrice, maxPrice);
		model.addAttribute("products", pageResponse.getItems());
		model.addAttribute("pageNo", pageResponse.getPageNo());
        model.addAttribute("totalPage", pageResponse.getTotalPage());
		return "shop";
	}
	
	@GetMapping("/product-by-brand")
	public String FilterByBrand(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize,
			@RequestParam String brand,
			Model model) {
		
		Brands brands = parseBrand(brand);
		try {
	        //Brands brandEnum = Brands.valueOf(brand.toUpperCase()); // chuyển từ "nike" → Brands.Nike
	        PageResponse<?> pageResponse = productService.getProductByBrand( pageNo, pageSize, brands);
			model.addAttribute("products", pageResponse.getItems());
			model.addAttribute("pageNo", pageResponse.getPageNo());
	        model.addAttribute("totalPage", pageResponse.getTotalPage());
	        return "shop";
	    } catch (IllegalArgumentException e) {
	    	model.addAttribute("BrandNotFound", true);
	        return "shop"; // hoặc thông báo không tìm thấy brand
	    }	
	}
	
	@GetMapping("/product-by-group")
	public String FilterByGroup(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize,
			@RequestParam String group,
			Model model) {
		
		GroupProduct groups = parseGroup(group);
		try {
	        //Brands brandEnum = Brands.valueOf(brand.toUpperCase()); // chuyển từ "nike" → Brands.Nike
	        PageResponse<?> pageResponse = productService.getProductByGroup( pageNo, pageSize, groups);
			model.addAttribute("products", pageResponse.getItems());
			model.addAttribute("pageNo", pageResponse.getPageNo());
	        model.addAttribute("totalPage", pageResponse.getTotalPage());
	        return "shop";
	    } catch (IllegalArgumentException e) {
	    	model.addAttribute("GroupsNotFound", true);
	        return "shop"; // hoặc thông báo không tìm thấy brand
	    }	
	}

	@GetMapping("/signle-post")
	public String singlePost() {
		return "signle-post";
	}


	@GetMapping("/login")
	public String login1() {
		return "login";
	}

	@GetMapping("/about.html")
	public String about() {
		return "about";
	}

	@GetMapping("/cart")
	public String Cart() {
		return "cart";
	}

	@GetMapping("/signUp.html")
	public String signUp(Model model) {
		UserRequestDTO account = new UserRequestDTO();
		account.getAddresses().add(new AddressDTO());
		model.addAttribute("account", account);
		return "signUp";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("account") UserRequestDTO userRequestDTO
	// BindingResult bindingResult,
	) {

		for (AddressDTO addr : userRequestDTO.getAddresses()) {
			log.info("Apartment: {}, Floor: {}, City: {}", addr.getApartmentNumber(), addr.getFloor(), addr.getCity());
		}

		try {
			userService.saveUser(userRequestDTO);
			log.info("user add success");
		} catch (Exception e) {
			log.error("errorMessage =  {}", e.getMessage(), e.getCause());
		}

		// Trả về trang đăng ký thành công
		return "registerSuccess"; // Template trang thành công
	}
	
	private Brands parseBrand(String input) {
        for (Brands b : Brands.values()) {
            if (b.name().equalsIgnoreCase(input)) {
                return b;
            }
        }
        return null;
    }
	
	private GroupProduct parseGroup(String input) {
	    for (GroupProduct g : GroupProduct.values()) {
	        if (g.name().equalsIgnoreCase(input)) {
	            return g;
	        }
	    }
	    return null;
	}

}
