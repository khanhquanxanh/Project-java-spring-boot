package com.Project1.demo.sevice;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.Project1.demo.dto.request.UserRequestDTO;
import com.Project1.demo.dto.response.PageResponse;
import com.Project1.demo.dto.response.UserDetailResponse;
import com.Project1.demo.model.User;
import com.Project1.demo.util.UserStatus;

import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Min;

public interface UserServer {
	
	long saveUser (UserRequestDTO request) throws UnsupportedEncodingException, MessagingException;
	
	void updateUser(long userId, UserRequestDTO request);
	
	void changeStatus(long userId, UserStatus status);
	
	void deleteUser(long userId);
	
	UserDetailResponse getUser(long userId);
	
	User getUserById(long id);
	
	User getUserByUsername (String username);
	
//	List<UserDetailResponse> getAllUsers(int pageNo, int pageSize, String sortBy);
	PageResponse getAllUsers(int pageNo, int pageSize, String... sort);
	
	PageResponse getAllUsersWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize,String search, String sortBy);
	
	PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);
	
	PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);

	String confirmUser(@Min(1) int userId, String verifyCode);
}
