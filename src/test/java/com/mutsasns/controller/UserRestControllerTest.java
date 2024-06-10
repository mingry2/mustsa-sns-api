package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.dto.user.join.UserJoinRequest;
import com.mutsasns.domain.dto.user.join.UserJoinResponse;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	UserService userService;
	@Autowired
	ObjectMapper objectMapper;
	UserJoinRequest userJoinRequest;
	UserJoinResponse userJoinResponse;

	@BeforeEach
	void setUP() {
		userJoinRequest = new UserJoinRequest("mingyeong", "kmk1234");
		userJoinResponse = new UserJoinResponse(1L, "mingyeong");
	}

	@Nested
	class join_test {

		@Test
		@DisplayName("회원가입 성공")
		@WithMockUser
		void join_success() throws Exception {

			when(userService.join(any(), any()))
                    .thenReturn(userJoinResponse);

			mockMvc.perform(post("/api/v1/users/join")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userJoinRequest)))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.resultCode").value("SUCCESS"));
		}

		@Test
		@DisplayName("회원가입 실패 - userName 중복")
		@WithMockUser
		void join_fail() throws Exception {

			when(userService.join(any(), any()))
					.thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME,
							ErrorCode.DUPLICATED_USER_NAME.getMessage()));

			mockMvc.perform(post("/api/v1/users/join")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userJoinRequest)))
					.andDo(print())
					.andExpect(jsonPath("$.result.errorCode").value("DUPLICATED_USER_NAME"))
					.andExpect(jsonPath("$.result.message").value("UserName이 중복됩니다."))
					.andExpect(status().isConflict());
		}

	}

	@Nested
	class login_test {

		@Test
		@DisplayName("로그인 성공")
		@WithMockUser
		void login_success() throws Exception {

			when(userService.login(any(), any()))
					.thenReturn("token");

			mockMvc.perform(post("/api/v1/users/login")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userJoinRequest)))
					.andDo(print())
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("로그인 실패 - userName 없음")
		@WithMockUser
		void login_fail1() throws Exception {

			when(userService.login(any(), any()))
					.thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND,
							ErrorCode.USERNAME_NOT_FOUND.getMessage()));

			mockMvc.perform(post("/api/v1/users/login")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userJoinRequest)))
					.andDo(print())
					.andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
					.andExpect(jsonPath("$.result.message").value("Not founded"))
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("로그인 실패 - password 틀림")
		@WithMockUser
		void login_fail2() throws Exception {

			when(userService.login(any(), any()))
					.thenThrow(new AppException(ErrorCode.INVALID_PASSWORD,
							ErrorCode.INVALID_PASSWORD.getMessage()));

			mockMvc.perform(post("/api/v1/users/login")
							.with(csrf())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsBytes(userJoinRequest)))
					.andDo(print())
					.andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
					.andExpect(jsonPath("$.result.message").value("패스워드가 잘못되었습니다."))
					.andExpect(status().isUnauthorized());
		}

	}

}