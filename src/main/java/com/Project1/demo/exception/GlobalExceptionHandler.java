package com.Project1.demo.exception;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.http.HttpStatus.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import lombok.Getter;
import lombok.Setter;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(Exception e, WebRequest request) {
		ErrorResponse errorReponse = new ErrorResponse();
		errorReponse.setTimestamp(new Date());
		errorReponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorReponse.setPath(request.getDescription(false).replace("uri", ""));
		errorReponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		
		String message = e.getMessage();
		if (e instanceof MethodArgumentNotValidException) {
		int start = message.lastIndexOf("[");
		int end = message.lastIndexOf("]");
		message = message.substring(start+1, end -1);
		errorReponse.setError("Payload in valid");
		//errorReponse.setMessage(message);
		}
		else if(e instanceof ConstraintViolationException) {
			message = message.substring(message.indexOf(" ")+1);
			//errorReponse.setMessage(message);
			errorReponse.setError("PathVariable in valid");
		}
		
		errorReponse.setMessage(message);
		return errorReponse;
	}
	
	@ExceptionHandler({MethodArgumentTypeMismatchException.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleInternalServerErrorException(Exception e, WebRequest request) {
		ErrorResponse errorReponse = new ErrorResponse();
		errorReponse.setTimestamp(new Date());
		errorReponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorReponse.setPath(request.getDescription(false).replace("uri", ""));
		errorReponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		if (e instanceof MethodArgumentTypeMismatchException) {
			errorReponse.setMessage("Failed to convert value of type");
		}
		return errorReponse;
	}
	
	 /**
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({AccessDeniedException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "403 Response",
                                    summary = "Handle exception when access forbidden",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:07:35.321+00:00\",\n" +
                                            "  \"status\": 403,\n" +
                                            "  \"path\": \"/api/v1/...\",\n" +
                                            "  \"error\": \"Forbidden\",\n" +
                                            "  \"message\": \"{data} not found\"\n" +
                                            "}"
                                ))})
                    })
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(FORBIDDEN.value());
        errorResponse.setError(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }
    
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "401 Response",
                                    summary = "Handle exception when resource not found",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:07:35.321+00:00\",\n" +
                                            "  \"status\": 401,\n" +
                                            "  \"path\": \"/api/v1/...\",\n" +
                                            "  \"error\": \"Unauthorized\",\n" +
                                            "  \"message\": \"Username or password is incorrect\"\n" +
                                            "}"
                                ))})
                    })
              
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setError(UNAUTHORIZED.getReasonPhrase());
        errorResponse.setMessage("Username or password is incorrect");

        return errorResponse;
    }
    
    @Getter
    @Setter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String message;
    }
}
