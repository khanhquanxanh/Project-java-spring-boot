package com.Project1.demo.sevice;

import com.Project1.demo.controller.LoginUserResponse;
import com.Project1.demo.controller.request.SignInRequest;

public interface LoginService {
	
	LoginUserResponse LoginUser(SignInRequest request);

}
