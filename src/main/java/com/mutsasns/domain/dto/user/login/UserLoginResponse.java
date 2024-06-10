package com.mutsasns.domain.dto.user.login;

import lombok.Getter;

@Getter
public class UserLoginResponse {
    private String jwt;

    public UserLoginResponse(String jwt) {
        this.jwt = jwt;
    }
}
