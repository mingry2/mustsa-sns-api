package com.mutsasns.domain.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mutsasns.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@Slf4j
public class PostListResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastModifiedAt;

    public static Page<PostListResponse> toResponse(Page<Post> postList) {
        Page<PostListResponse> postListResponse = postList.map(post -> PostListResponse.builder()
                .id(post.getPostId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build());

        return postListResponse;
    }

}
