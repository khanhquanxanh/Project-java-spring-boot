package com.Project1.demo.sevice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.Project1.demo.controller.LoginUserResponse;
import com.Project1.demo.controller.TokenResponse;
import com.Project1.demo.controller.request.SignInRequest;
import com.Project1.demo.sevice.JwtService;
import com.Project1.demo.sevice.LoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtService jwtService;

	@Override
	public LoginUserResponse LoginUser(SignInRequest request) {

		log.info("user authentication");
		
		String redirectUrl = "/view/home"; 
		
		String roles;
		
		List<String> authorities = new ArrayList<>();
		// Thực hiện xác thực với username và password
		try {
            // Thực hiện xác thực với username và password
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated = {}", authenticate.isAuthenticated());
            roles = authenticate.getAuthorities().toString();
            log.info("userName: {}", request.getUsername());
            log.info("Authorities: {}", roles);
            authorities.add(authenticate.getAuthorities().toString());

            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (Exception e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
		
		if ("[Admin]".equals(roles)) {
	        redirectUrl = "/view/admin";
	        
	    }
	    if ("[Manager]".equals(roles)) {
	        redirectUrl = "/view/manager";
	    }

        // Get user
        //var user = userRepository.findByUsername(request.getUsername());
        
		String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
		
		String refreshToken = jwtService.refreshAccessToken(request.getUsername(), authorities);
		
		return LoginUserResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.userName(request.getUsername())
				.authorities(roles)
				.redirectUrl(redirectUrl)
				.build();
	}

}
