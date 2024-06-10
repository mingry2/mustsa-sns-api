package com.mutsasns.domain.dto.user.join;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class UserJoinResponse {
    private Long userId;
    private String userName;

}
