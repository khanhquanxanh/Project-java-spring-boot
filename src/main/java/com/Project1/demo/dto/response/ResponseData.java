package com.Project1.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseData<T> {
	
	private final int status;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;
	
	//put, patch, dele
	public ResponseData(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	//get, post
	public ResponseData(int status, String message, T data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
