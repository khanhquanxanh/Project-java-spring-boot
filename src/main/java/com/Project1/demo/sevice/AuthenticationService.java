package com.Project1.demo.sevice;

import com.Project1.demo.controller.TokenResponse;
import com.Project1.demo.controller.request.SignInRequest;

public interface AuthenticationService {

	TokenResponse getAccessToken(SignInRequest request);
	
	TokenResponse getRefreshToken(String request);
	
}
