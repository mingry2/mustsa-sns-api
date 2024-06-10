package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.dto.like.LikeAddResponse;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeRestController.class)
class LikeRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    LikeService likeService;
    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class like {
        @Test
        @DisplayName("좋아요 누르기 성공")
        @WithMockUser
        void add_like() throws Exception {

            when(likeService.addLike(any(), any()))
                    .thenReturn(new LikeAddResponse());

            mockMvc.perform(post("/api/v1/posts/1/likes")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("좋아요를 눌렀습니다."));
        }

        @Test
        @DisplayName("좋아요 누르기 실패(1) - 로그인 하지 않은 경우")
        @WithMockUser
        void create_fail1() throws Exception {

            when(likeService.addLike(any(), any()))
                    .thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/posts/1/likes")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.USERNAME_NOT_FOUND.getHttpStatus().value()))
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.message").value("Not founded"));
        }

        @Test
        @DisplayName("좋아요 누르기 실패(2) - 해당 Post가 없는 경우")
        @WithMockUser
        void create_fail2() throws Exception {

            when(likeService.addLike(any(), any()))
                    .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));

            mockMvc.perform(post("/api/v1/posts/1/likes")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getHttpStatus().value()))
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.message").value("해당 포스트가 없습니다."));
        }

    }

}