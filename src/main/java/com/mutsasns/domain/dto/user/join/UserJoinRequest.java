package com.mutsasns.domain.dto.user.join;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class UserJoinRequest {
    private String userName;
    private String password;
}
