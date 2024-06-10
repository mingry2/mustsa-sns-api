package com.mutsasns.exception;

import com.mutsasns.domain.dto.response.Response;
import com.mutsasns.domain.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

	@ExceptionHandler(AppException.class)
	public ResponseEntity<?> appExceptionHandler(AppException e) {
		ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());

		return ResponseEntity.status(e.getErrorCode().getHttpStatus())
				.body(Response.error(errorResponse));
	}

}
