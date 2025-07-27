package com.Project1.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Project1.demo.controller.request.SignInRequest;
import com.Project1.demo.model.User;
import com.Project1.demo.sevice.AuthenticationService;
import com.Project1.demo.sevice.LoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final LoginService loginService;
	
	private final AuthenticationService authenticationService;
	
	
	//Get access token
	@PostMapping("/access-token")
	public TokenResponse getAccessToken(@RequestBody SignInRequest user) {
		log.info("access token request");
		//return TokenResponse.builder().accessToken("ACCESS-TOKEN").refreshToken("REFRESH-TOKEN").build();
		return authenticationService.getAccessToken(user);
	}
	
	//get Token user refresh token
	@PostMapping("/refresh-token")
    public TokenResponse refreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");

        return authenticationService.getRefreshToken(refreshToken);
    }
	
	// login user
	@PostMapping("/login")
	public LoginUserResponse login(@RequestBody SignInRequest loginRequest) {
	    
		log.info("Login and get access token");
		
		return loginService.LoginUser(loginRequest);
	}
}
