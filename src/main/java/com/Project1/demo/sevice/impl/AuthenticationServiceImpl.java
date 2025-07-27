package com.Project1.demo.sevice.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import com.Project1.demo.controller.TokenResponse;
import com.Project1.demo.controller.request.SignInRequest;
import com.Project1.demo.exception.ForBiddenException;
import com.Project1.demo.exception.InvalidDataException;
import com.Project1.demo.model.User;
import com.Project1.demo.repository.UserRepository;
import com.Project1.demo.sevice.AuthenticationService;
import com.Project1.demo.sevice.JwtService;


import lombok.RequiredArgsConstructor;
//import lombok.var;
import lombok.extern.slf4j.Slf4j;

import static com.Project1.demo.util.TokenType.REFRESH_TOKEN;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService{

	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	@Override
	public TokenResponse getAccessToken(SignInRequest request) {
		log.info("Get access token");
		
		List<String> authorities = new ArrayList<>();
		// Thực hiện xác thực với username và password
		try {
            // Thực hiện xác thực với username và password
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated = {}", authenticate.isAuthenticated());
            log.info("Authorities: {}", authenticate.getAuthorities().toString());
            authorities.add(authenticate.getAuthorities().toString());

            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (BadCredentialsException | DisabledException | InternalAuthenticationServiceException e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new InternalAuthenticationServiceException(e.getMessage());
        }

        // Get user
        //var user = userRepository.findByUsername(request.getUsername());
        
		String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
		
		String refreshToken = jwtService.refreshAccessToken(request.getUsername(), authorities);
		
		return TokenResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	@Override
	public TokenResponse getRefreshToken(String refreshToken) {
		log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        
        try {
            // Verify token
            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

            // check user is active or inactivated
            User user = userRepository.findByUsername(userName);
            
            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            // generate new access token
            String accessToken = jwtService.generateAccessToken(user.getUsername(), authorities);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForBiddenException(e.getMessage());
        }
	}

}
