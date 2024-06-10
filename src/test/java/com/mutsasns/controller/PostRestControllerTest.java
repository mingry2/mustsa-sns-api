package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.dto.post.PostRequest;
import com.mutsasns.domain.dto.post.PostResponse;
import com.mutsasns.domain.dto.post.PostListResponse;
import com.mutsasns.domain.entity.Post;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
class PostRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PostService postService;
    @Autowired
    ObjectMapper objectMapper;
    PostRequest postRequest = new PostRequest("test title", "test body"); // 글 등록 시 유저가 작성한 title, body
    PostRequest modifyRequest = new PostRequest("test modify title", "test modify body"); // 글 수정 시 유저가 수정한 modify title, modify body

    @Nested
    class post_create_test{
        @Test
        @DisplayName("포스트 등록 완료")
        @WithMockUser
        void create() throws Exception {

            when(postService.create(any(), any(), any()))
                    .thenReturn(PostResponse.builder()
                            .message("포스트 등록 완료")
                            .postId(1L)
                            .build());

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                    .andExpect(jsonPath("$.result.postId").value(1L));
        }

        @Test
        @DisplayName("포스트 작성 실패(1) - 인증실패(JWT를 Bearer Token으로 보내지 않은 경우")
        @WithMockUser
        void create_fail1() throws Exception {

            when(postService.create(any(), any(), any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage()));

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                    .andExpect(jsonPath("$.result.message").value("잘못된 토큰입니다."));
        }

        @Test
        @DisplayName("포스트 작성 실패(2) - 인증실패(JWT가 유효하지 않은 경우)")
        @WithMockUser
        void create_fail2() throws Exception {

            when(postService.create(any(), any(), any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage()));

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(postRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                    .andExpect(jsonPath("$.result.message").value("잘못된 토큰입니다."));
        }

    }

    @Nested
    class post_get_test{
        @Test
        @DisplayName("포스트 1개 조회 성공")
        @WithMockUser
        void post_get_success() throws Exception {

            PostListResponse postListResponse = PostListResponse.builder()
                    .id(2L)
                    .title("test title")
                    .body("test body")
                    .userName("test userName")
                    .createdAt(LocalDateTime.now())
                    .lastModifiedAt(LocalDateTime.now())
                    .build();

            when(postService.getPost(any()))
                    .thenReturn(postListResponse);

            mockMvc.perform(get("/api/v1/posts/2")
                            .with(csrf()))
                    // result 안에 감싸져있기 때문에 .result를 통해 경로를 지정해준다.
                    .andExpect(jsonPath("$.result.id").exists())
                    .andExpect(jsonPath("$.result.title").exists())
                    .andExpect(jsonPath("$.result.body").exists())
                    .andExpect(jsonPath("$.result.userName").exists())
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("포스트 전체 조회 성공 - pageable 사용")
        @WithMockUser
        void post_get_all_success() throws Exception {

            mockMvc.perform(get("/api/v1/posts")
                            .param("page", "0")
                            .param("size", "3")
                            .param("sort", "createdAt,desc"))
                    .andExpect(status().isOk());

            // ArgumentCaptor 사용 -> 객체를 호출할 때 사용한 인자를 검증할 때 사용
            // 메소드 호출 여부를 검증하는 과정에서 실제 호출할 때 전달한 인자를 보관할 수 있음
            // capture() 메소드를 전달 getValue() 메소드로 실제 인자 값을 가져와서 검증
            ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(postService).getAll(pageableArgumentCaptor.capture());
            PageRequest pageRequest = (PageRequest) pageableArgumentCaptor.getValue();

            assertEquals(0, pageRequest.getPageNumber());
            assertEquals(3, pageRequest.getPageSize());
            assertEquals(Sort.by("createdAt", "desc"), pageRequest.withSort(Sort.by("createdAt","desc")).getSort());
        }

    }

    @Nested
    class post_modify_test{
        @Test
        @DisplayName("포스트 수정 성공")
        @WithMockUser // 인증한 사용자를 테스트에서 사용한 경우
        void post_modify_success() throws Exception {

            // 수정 할 postId
            Post post = Post.builder()
                    .postId(1L)
                    .build();

            // Post -> PostResponse
            PostResponse postModifyResponse = PostResponse.builder()
                    .message("포스트 수정 완료")
                    .postId(post.getPostId())
                    .build();

            // 인증, postId, request.title, request.body
            when(postService.modify(any(),any(),any(),any()))
                    .thenReturn(postModifyResponse);

            mockMvc.perform(put("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                    .andExpect(jsonPath("$.result.postId").value(1L))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("포스트 수정 실패(1) - 인증 실패")
        @WithAnonymousUser // 인증되지 않은 사용자를 테스트에서 사용할 경우
        void post_modify_fail1() throws Exception {

            // 인증, postId, title, body -> 인증.getName의 userName이 권한 없음
            when(postService.modify(any(),any(),any(),any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(put("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("포스트 수정 실패(2) - 포스트 내용 불일치")
        @WithMockUser
        void post_modify_fail2() throws Exception {

            // 인증, postId, title, body -> postId에 해당하는 포스트 없음
            when(postService.modify(any(),any(),any(),any()))
                    .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND,ErrorCode.POST_NOT_FOUND.getMessage()));

            mockMvc.perform(put("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    // ErrorCode에 정의되어있는 에러의 HttpStatus 값 가져오기
                    .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getHttpStatus().value()));
        }

        @Test
        @DisplayName("포스트 수정 실패(3) - 작성자 불일치")
        @WithMockUser
        void post_modify_fail3() throws Exception {

            // 인증, postId, title, body -> token의 userName(작성자)과 수정 할 postId의 userName이 다를 경우
            when(postService.modify(any(),any(),any(),any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(put("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    // ErrorCode에 정의되어있는 에러의 HttpStatus 값 가져오기
                    .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
        }

        @Test
        @DisplayName("포스트 수정 실패(4) - 데이터베이스 에러")
        @WithMockUser
        void post_modify_fail4() throws Exception {

            // 인증, postId, title, body -> 수정완료하려고 하는데 갑자기 데이터베이스와의 연결이 끊어져 확인할 수 없는 경우
            when(postService.modify(any(),any(),any(),any()))
                    .thenThrow(new AppException(ErrorCode.DATABASE_ERROR,ErrorCode.DATABASE_ERROR.getMessage()));

            mockMvc.perform(put("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    // ErrorCode에 정의되어있는 에러의 HttpStatus 값 가져오기
                    .andExpect(status().is(ErrorCode.DATABASE_ERROR.getHttpStatus().value()));
        }

    }

    @Nested
    class post_delete_test{
        @Test
        @DisplayName("포스트 삭제 성공")
        @WithMockUser
        void post_delete_success() throws Exception {

            mockMvc.perform(delete("/api/v1/posts/1")
                            .with(csrf())
                            // json 형식으로 변경
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                    .andExpect(jsonPath("$.result.postId").value(1L))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("포스트 삭제 실패(1) - 인증 실패")
        @WithAnonymousUser
        void post_delete_fail1() throws Exception {
            // 인증, postId -> 인증.getName의 userName이 권한 없음
            when(postService.delete(any(),any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(delete("/api/v1/posts/1")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("포스트 삭제 실패(2) - 작성자 불일치")
        @WithMockUser
        void post_delete_fail2() throws Exception {
            // 인증, postId -> postId와 userId가 다름
            when(postService.delete(any(),any()))
                    .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage()));

            mockMvc.perform(delete("/api/v1/posts/1")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("포스트 삭제 실패(3) - 데이터베이스 에러")
        @WithMockUser
        void post_delete_fail3() throws Exception {
            // 삭제하려고 하는데 갑자기 데이터베이스와의 연결이 끊어져 확인할 수 없는 경우
            when(postService.delete(any(),any()))
                    .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMessage()));

            mockMvc.perform(delete("/api/v1/posts/1")
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.DATABASE_ERROR.getHttpStatus().value()));
        }

    }








}