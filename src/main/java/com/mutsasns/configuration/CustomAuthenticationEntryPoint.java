package com.mutsasns.configuration;

import com.mutsasns.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		ErrorCode exception = (ErrorCode) request.getAttribute("invalidTokenException");

		if (exception.equals(ErrorCode.INVALID_TOKEN)) {
			setResponse(response, ErrorCode.INVALID_TOKEN);
		}
	}

	private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		JSONObject responseJson = new JSONObject();
		responseJson.put("message", errorCode.getMessage());
		responseJson.put("code", errorCode.getHttpStatus());

		response.getWriter().print(responseJson);
	}
}
