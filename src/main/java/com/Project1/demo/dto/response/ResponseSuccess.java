package com.Project1.demo.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload>{
	
	// put, path,dele
	public ResponseSuccess(HttpStatusCode status, String message) {
		super(new Payload(status.value(),message), HttpStatus.OK);
		// TODO Auto-generated constructor stub
	}
	
	//get, post
	public ResponseSuccess(HttpStatusCode status, String message, Object data) {
		super(new Payload(status.value(),message, data), HttpStatus.OK);
		// TODO Auto-generated constructor stub
	}

	public static class Payload{
		private final int status;
		private final String message;
		private Object data;
		public Payload(int status, String message) {
			super();
			this.status = status;
			this.message = message;
		}
		public Payload(int status, String message, Object data) {
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
		public Object getData() {
			return data;
		}
		
		
	}
}
