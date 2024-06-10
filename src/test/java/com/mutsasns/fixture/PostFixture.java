package com.mutsasns.fixture;

import com.mutsasns.domain.entity.Post;

public class PostFixture {
    public static Post get(String userName, String password) {
        Post post = Post.builder()
                .postId(1L)
                .user(UserFixture.get(userName, password))
                .title("title")
                .body("body")
                .build();

        return post;
    }

}
