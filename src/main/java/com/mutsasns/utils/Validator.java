package com.mutsasns.utils;

import com.mutsasns.domain.entity.Comment;
import com.mutsasns.domain.entity.Post;
import com.mutsasns.domain.entity.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.CommentRepository;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@Builder
@Slf4j
public class Validator { // Service에서 중복되는 로직 분리

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public Validator(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // userName이 있는지 체크
    public User validatorUser(String userName){
        log.debug("userName : {}", userName);

        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));
    }

    // postId가 있는지 체크
    public Post validatorPost(Long postId){

        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ErrorCode.POST_NOT_FOUND.getMessage()));
    }

    // commentId가 있는지 체크
    public Comment validatorComment(Long id){

        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }

    // comment를 작성한 userId와 comment를 수정할 userId가 같은지 체크
    public void validatorEqualsUserId(Long commentWriteUserId, Long loginUserId){
        if (!Objects.equals(commentWriteUserId, loginUserId)){
            throw new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

}
