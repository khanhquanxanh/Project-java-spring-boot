package com.Project1.demo.sevice;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.Project1.demo.util.TokenType;

public interface JwtService {

	String generateAccessToken(String username, List<String> authorities);
	
	String refreshAccessToken(String username, List<String> authorities);
	
	String extractUsername(String token, TokenType type);
}
