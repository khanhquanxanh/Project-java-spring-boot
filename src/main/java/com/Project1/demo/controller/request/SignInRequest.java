package com.Project1.demo.controller.request;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class SignInRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private String password;
    private String platform; // web, mobile, tablet
    private String deviceToken; // for push notify
    private String versionApp;
}
