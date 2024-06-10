package com.mutsasns.fixture;

import com.mutsasns.domain.entity.User;
import com.mutsasns.domain.entity.UserBaseEntity;
import com.mutsasns.domain.entity.UserRole;
import java.sql.Timestamp;
import java.time.Instant;

public class UserFixture extends UserBaseEntity {

    public static User get(String userName, String password) {
        User user = new User();

        user.setUserId(1L);
        user.setUserName(userName);
        user.setPassword(password);
        user.setRole(UserRole.USER);
        user.setRegisteredAt(Timestamp.from(Instant.now()).toLocalDateTime());

        return user;
    }


}
