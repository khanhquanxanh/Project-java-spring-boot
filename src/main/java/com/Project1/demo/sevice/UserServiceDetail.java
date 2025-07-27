package com.Project1.demo.sevice;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.Project1.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceDetail {
	
	private final UserRepository userRepository;
	
	public UserDetailsService userServiceDetail() {
		return userRepository::findByUsername;
	}
}
