package com.mutsasns.domain.dto.user.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLoginRequest {
    private String userName;
    private String password;

}
