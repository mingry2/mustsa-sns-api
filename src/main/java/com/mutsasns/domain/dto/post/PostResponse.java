package com.mutsasns.domain.dto.post;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class PostResponse {
    private String message;
    private Long postId;

}
