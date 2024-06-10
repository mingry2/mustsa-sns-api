package com.mutsasns.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostRequest {
    private String title;
    private String body;

}
