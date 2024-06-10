package com.mutsasns.domain.dto.response;

import com.mutsasns.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {

	private ErrorCode errorCode;
	private String message;
}
