package com.mutsasns.service;

import com.mutsasns.domain.dto.post.PostResponse;
import com.mutsasns.domain.dto.post.PostListResponse;
import com.mutsasns.domain.entity.*;
import com.mutsasns.repository.*;
import com.mutsasns.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;
    private final Validator validator;

    public PostService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository, AlarmRepository alarmRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.alarmRepository = alarmRepository;
        this.validator = Validator.builder()
                .userRepository(userRepository)
                .postRepository(postRepository)
                .build();
    }

    // 포스트 등록
    public PostResponse create(String title, String body, String userName) {

        // userName이 있는지 체크
        User user = validator.validatorUser(userName);

        // 포스트 등록 완료
        log.info("title : {} / body : {} ", title, body);
        Post post = Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();

        postRepository.save(post);

        return PostResponse.builder()
                .message("포스트 등록 완료")
                .postId(post.getPostId())
                .build();

    }

    // 포스트 전체 조회
    public Page<PostListResponse> getAll(Pageable pageable){
        Page<Post> postList = postRepository.findAll(pageable);
        Page<PostListResponse> postListResponses = PostListResponse.toResponse(postList);
        return postListResponses;
    }

    // 포스트 상세 조회
    public PostListResponse getPost(Long postId) {

        // postId가 있는지 체크
        Post post = validator.validatorPost(postId);

        // 포스트 조회 완료
        return PostListResponse.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }

    // 포스트 수정
    @Transactional
    public PostResponse modify(String userName, Long postId, String title, String body) {

        // userName이 있는지 체크
        User user = validator.validatorUser(userName);

        // postId가 있는지 체크
        Post post = validator.validatorPost(postId);

        // 로그인 유저가 포스트 작성유저가 아닐 경우
        Long loginUserId = user.getUserId();
        Long postWriteUserId = post.getUser().getUserId();

        validator.validatorEqualsUserId(postWriteUserId, loginUserId);

        // 수정 포스트 저장
        post.setTitle(title); // PostModifyRequest에서 수정한 title로 post에 저장
        post.setBody(body); // PostModifyRequest에서 수정한 body로 post에 저장
        Post savedPost = postRepository.saveAndFlush(post);

        return PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(savedPost.getPostId())
                .build();
    }

    // 포스트 삭제
    @Transactional
    public PostResponse delete(Long postId, String userName) {

        // userName이 있는지 체크
        User user = validator.validatorUser(userName);

        // postId가 있는지 체크
        Post post = validator.validatorPost(postId);

        // 로그인 유저가 포스트 작성유저가 아닐 경우
        Long loginUserId = user.getUserId();
        Long postWriteUserId = post.getUser().getUserId();

        validator.validatorEqualsUserId(postWriteUserId, loginUserId);

        // 포스트 삭제
        // 포스트에 달린 댓글 삭제
        commentRepository.deleteAllByPost(post);

        // 포스트에 달린 좋아요 삭제
        likeRepository.deleteAllByPost(post);

        // 포스트 삭제
        postRepository.delete(post);

        return PostResponse.builder()
                .message("포스트 삭제 완료")
                .postId(postId)
                .build();
    }

    // 마이피드 조회
    public Page<PostListResponse> myFeedAll(String userName, Pageable pageable) {

        // userName이 있는지 체크
        User user = validator.validatorUser(userName);

        // userName으로 조회되는 포스트 모두 조회
        Page<Post> userPosts = postRepository.findAllByUser(user, pageable);
        Page<PostListResponse> postListResponses = PostListResponse.toResponse(userPosts);

        return postListResponses;

    }
}
