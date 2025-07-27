package com.Project1.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Project1.demo.configuration.Translator;
import com.Project1.demo.dto.request.OrderRequestDTO;
import com.Project1.demo.dto.request.UserRequestDTO;
import com.Project1.demo.dto.response.OrderResponseDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.ResponseData;
import com.Project1.demo.dto.response.ResponseError;
import com.Project1.demo.dto.response.UserDetailResponse;
import com.Project1.demo.exception.ResourceNotFoundException;
import com.Project1.demo.sevice.OrderService;
import com.Project1.demo.sevice.UserServer;
import com.Project1.demo.util.UserStatus;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserServer userService;
	
	private final OrderService orderService;

	@PostMapping(value = "/")
	public ResponseData<Long> adduser(@Valid @RequestBody UserRequestDTO userdto) {
		log.info("Request add user = {} {} ", userdto.getFistname(), userdto.getLastname());

		try {
			long userId = userService.saveUser(userdto);
			return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
		} catch (Exception e) {
			log.error("errorMessage =  {}", e.getMessage(), e.getCause());
			return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "add user fail");
		}
	}
	@PostMapping(value = "/register")
	public ResponseData<?> RegisterUser(UserRequestDTO account){
		if (userService.getUserByUsername(account.getUsername())== null) {
			return new ResponseError(1,"Username already exists");
		} else {
			try {
				long userId = userService.saveUser(account);
				return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
			} catch (Exception e) {
				log.error("errorMessage =  {}", e.getMessage(), e.getCause());
				return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "add user fail");
			}
		}
		
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('Admin', 'User')")
	public ResponseData<?> updateUser(@PathVariable @Min(1) int id, @Valid @RequestBody UserRequestDTO userdto) {
		System.out.println("Request update data id = " + id);
		log.info("updated user id = {}", id);
		try {
			userService.updateUser(id, userdto);
			return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
		} catch (Exception e) {
			log.error("ERROR_MESSAGE", e.getMessage(), e.getCause());
			return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
		}

	}

	@PatchMapping("/{userid}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseData<?> changeStatus(@Min(1) @PathVariable int userid, @RequestParam UserStatus status) {
		System.out.println("Request change status, user id =" + userid);
		log.info("Change user status id = {}", userid);
		try {
			userService.changeStatus(userid, status);
			return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
		} catch (Exception e) {
			log.info("ERROR_MESSAGE", e.getCause());
			return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change user Status fail");
		}

	}

	@DeleteMapping("/{userid}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int id) {
		System.out.println("Delete user id =" + id);
		log.info("Request delete userId={}", id);

		try {
			userService.deleteUser(id);
			return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
		} catch (Exception e) {
			log.error("ERROR_MESSAGE", e.getMessage(), e.getCause());
			return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
		}
	}

	@GetMapping("/{userId}")
	@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
	public ResponseData<UserDetailResponse> getUser(@PathVariable int userId) {
		System.out.println("Request get user detail by userId = " + userId);
		log.info("Request get user detail, userid = {}", userId);
		
		try {
			return new ResponseData<>(
					HttpStatus.OK.value(), 
					"user: ",
					userService.getUser(userId));
		}catch(ResourceNotFoundException e) {
			log.info("error Message = {}",e.getCause());
			return new ResponseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
		}
		
	}

//	@GetMapping("/list")
//	public ResponseData<List<UserDetailResponse>> getAllUser(
//			
//			@RequestParam(defaultValue = "0", required = false) int pageNo,
//			@Min(10) @RequestParam(defaultValue = "20",required = false) int pageSize,
//			@RequestParam(required = false) String sortBy) {
//		
//		System.out.println("Request get all USER");
//		return new ResponseData<>(HttpStatus.OK.value(), "user: ",userService.getAllUsers(pageNo, pageSize, sortBy));
//		
//	}

	@Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
	@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                  @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
												  @RequestParam(required = false) String... sorts) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> users = userService.getAllUsers(pageNo, pageSize, sorts);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error("ERROR_MESSAGE", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
	
	@GetMapping("/list-with-sort-by-multiple-columns-search")
    public ResponseData<?> getAllUsersWithSortByMultipleColumnsAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                @RequestParam(defaultValue = "20", required = false) int pageSize,
																@RequestParam(defaultValue = "20", required = false) String search,
                                                                @RequestParam(required = false) String sortsBy) {
        log.info("Request get all of users with sort by multiple columns");
        
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsersWithSortByMultipleColumnsAndSearch(pageNo, pageSize,search, sortsBy));
    }
	
	@GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(defaultValue = "") String... search) {
        log.info("Request advance search query by criteria");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithCriteria(pageNo, pageSize, sortBy, address, search));
    }
	
	@GetMapping("/advance-search-with-specification")
    public ResponseData<?> advanceSearchWithSpecification(Pageable pageable,
                                                     @RequestParam(required = false) String[] user,
                                                     @RequestParam(required = false) String[] address){
        log.info("Request advance search query by specification");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithSpecifications(pageable, user, address));
    }
	
	@GetMapping("/confirm/{userId}")
    public ResponseData<String> confirm(@Min(1) @PathVariable int userId, @RequestParam String verifyCode, HttpServletResponse response) throws IOException {
        log.info("Confirm user, userId={}, verifyCode={}", userId, verifyCode);

        try {
            userService.confirmUser(userId, verifyCode);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User has confirmed successfully");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Confirm was failed");
        } finally {
        	response.sendRedirect("http://localhost:8080/view/login");
        }
    }
	
	@GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("User name: {}", username);// hoặc ép kiểu để lấy thông tin sâu hơn
            return ResponseEntity.ok(Map.of("username", username));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization").replace("Bearer ", "");
        OrderResponseDTO result = orderService.createOrder(request, token);
        return ResponseEntity.ok(result);
    }
	

}
