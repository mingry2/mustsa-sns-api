package com.mutsasns.service;

import com.mutsasns.domain.dto.post.PostListResponse;
import com.mutsasns.domain.entity.Post;
import com.mutsasns.domain.entity.User;
import com.mutsasns.domain.entity.UserRole;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// ServiceTest는 어노테이션 없이 test가 가능하게하는 것을 지향함 -> pojo test
class PostServiceTest {

    PostService postService;

    // Mockito.mock을 이용하여 DB 디펜던시를 뺌 -> 스프링과 DB에 종속적이지 않음
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    LikeRepository likeRepository = Mockito.mock(LikeRepository.class);
    AlarmRepository alarmEntityRepository = Mockito.mock(AlarmRepository.class);

    @BeforeEach // 모든 test 실행전 먼저 실행되는 어노테이션
    void setUp() {
        postService = new PostService(userRepository, postRepository, commentRepository, likeRepository, alarmEntityRepository); // new를 사용하여 객체생성
    }
    String userName = "user";
    String password = "1234";
    User user = User.builder()
            .userId(1L)
            .userName(userName)
            .password(password)
            .role(UserRole.USER)
            .build();
    Post post = Post.builder()
            .postId(1L)
            .user(user)
            .title("test title")
            .body("test body")
            .build();

    @Test
    @DisplayName("포스트 1개 조회 성공")
    void get_post_success() {

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostListResponse postListResponse = postService.getPost(1L);
        assertEquals(user.getUserName(), postListResponse.getUserName());
    }

    @Nested // test를 계층적으로 구분할 때 사용 -> 가독성이 좋아짐
    class post_create_test{ // 포스트 등록 테스트
        @Test
        @DisplayName("포스트 등록 성공")
        void create_post_success() {

            User mockUser = mock(User.class);
            Post mockPost = mock(Post.class);

            when(userRepository.findByUserName(user.getUserName()))
                    .thenReturn(Optional.of(mockUser));
            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.of(mockPost));

            // Assertions -> 테스트가 원하는 결과를 제대로 리턴하는지 에러를 발생하지 않는지 확인할 때 사용하는 메소드
            // assertDoesNotThrow -> 에러가 발생하지 않으면 True
            Assertions.assertDoesNotThrow(
                    () -> postService.create(post.getTitle(), post.getBody(), post.getUser().getUserName()));
        }

        @Test
        @DisplayName("포스트 등록 실패 - 유저가 존재하지 않을 때")
        void create_post_fail() {

            when(userRepository.findByUserName(user.getUserName()))
                    .thenReturn(Optional.empty());
            when(postRepository.save(any()))
                    .thenReturn(mock(Post.class));

            // Assertions -> 테스트가 원하는 결과를 제대로 리턴하는지 에러를 발생하지 않는지 확인할 때 사용하는 메소드
            // .assertThrows -> 예상한 에러가 발생하면 True
            AppException exception = Assertions.assertThrows(AppException.class,
                    () -> postService.create(post.getUser().getUserName(), post.getTitle(),post.getBody()));

            assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    class post_modify_test{ // 포스트 수정 테스트
        @Test
        @DisplayName("포스트 수정 실패(1): 작성자!=유저")
        void modify_fail1(){

            // 다른 유저
            String userName1 = "userName2";
            String password1 = "1q2w3e4r2";
            User user1 = User.builder()
                    .userId(2L)
                    .userName(userName1)
                    .password(password1)
                    .role(UserRole.USER)
                    .build();

            when(userRepository.findByUserName(user.getUserName()))
                    .thenReturn(Optional.of(user1));

            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.of(post));

            // 포스트 수정하는 유저는 user != 포스트를 작성한 유저는 user1
            AppException exception = Assertions.assertThrows(AppException.class,
                    () -> postService.modify(post.getUser().getUserName(), post.getPostId(), post.getTitle(), post.getBody()));

            assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
        }

        @Test
        @DisplayName("포스트 수정 실패(2): 포스트 존재하지 않음")
        void modify_fail2(){

            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.empty());

            AppException exception = Assertions.assertThrows(AppException.class,
                    // String.valueOf()를 사용하면 전달받은 파라미터가 null이 전달될 경우 문자열 "null"을 반환
                    () -> postService.modify(String.valueOf(post.getUser().getUserId()), post.getPostId(), post.getTitle(), post.getBody()));

            assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("포스트 수정 실패(3): 유저 존재하지 않음")
        void modify_fail3(){

            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.of(mock(Post.class)));

            when(userRepository.findByUserName(post.getUser().getUserName()))
                    .thenReturn(Optional.empty());

            AppException exception = Assertions.assertThrows(AppException.class,
                    // String.valueOf()를 사용하면 전달받은 파라미터가 null이 전달될 경우 문자열 "null"을 반환
                    () -> postService.modify(String.valueOf(post.getUser().getUserId()), post.getPostId(), post.getTitle(), post.getBody()));

            assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
        }

    }

    @Nested
    class post_delete_test{ // 포스트 삭제 테스트
//        @Test
//        @DisplayName("포스트 삭제 성공")
//        void post_delete_success() {
//
//            Post mockPost = mock(Post.class);
//            User mockUser = mock(User.class);
//
//            when(postRepository.findById(post.getPostId()))
//                    .thenReturn(Optional.of(mockPost));
//            System.out.println(post.getPostId());
//
//            when(userRepository.findByUserName(post.getUser().getUserName()))
//                    .thenReturn(Optional.of(user));
//            System.out.println(post.getUser().getUserName());
//
//            when(mockPost.getUser())
//                    .thenReturn(user);
//            System.out.println(user.getUserId());
//            System.out.println(user.getUserName());
//
//            PostResponse postDeletedResponse = postService.delete(post.getPostId(), user.getUserName());
//            assertEquals(postDeletedResponse.getMessage(), "포스트 삭제 완료");
//        }

        @Test
        @DisplayName("포스트 삭제 실패(1): 유저 존재하지 않음")
        void post_delete_fail1() {

            Post mockPost = mock(Post.class);
            User mockUser = mock(User.class);

            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.of(mockPost));

            when(userRepository.findByUserName(post.getUser().getUserName()))
                    .thenReturn(Optional.empty());

            AppException exception = Assertions.assertThrows(AppException.class,
                    () -> postService.delete(post.getPostId(), post.getUser().getUserName()));
            assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("포스트 삭제 실패(2): 포스트 존재하지 않음")
        void post_delete_fail2() {

            Post mockPost = mock(Post.class);
            User mockUser = mock(User.class);

            when(postRepository.findById(post.getPostId()))
                    .thenReturn(Optional.empty());

            when(userRepository.findByUserName(post.getUser().getUserName()))
                    .thenReturn(Optional.of(user));

            AppException exception = Assertions.assertThrows(AppException.class,
                    () -> postService.delete(post.getPostId(), post.getUser().getUserName()));
            assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        }

    }

}