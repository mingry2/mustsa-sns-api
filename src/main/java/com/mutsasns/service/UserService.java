package com.mutsasns.service;

import com.mutsasns.domain.dto.user.join.UserJoinResponse;
import com.mutsasns.domain.entity.User;
import com.mutsasns.domain.entity.UserRole;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.UserRepository;
import com.mutsasns.utils.JwtTokenUtil;
import com.mutsasns.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    // token 설정
    @Value("${jwt.token.secret}")
    private String secretKey;
    //    private Long expiredTimeMs = 1000 * 60 * 60L; // 1시간
    private Long expiredTimeMs = 1 * 1000 * 60 * 60L * 24; // 하루

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final Validator validator;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder){
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.validator = Validator.builder()
                .userRepository(userRepository)
                .build();

    }

    // 회원가입
    public UserJoinResponse join(String userName, String password) {
        // userName 중복 체크
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, ErrorCode.DUPLICATED_USER_NAME.getMessage());
                });

        // DB 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return UserJoinResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .build();
    }

    // 로그인
    public String login(String userName, String password) {
        // userName이 있는지 체크
        User user = validator.validatorUser(userName);

        // password 맞는지 체크
        if (!encoder.matches(password, user.getPassword()))
            throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());

        // 로그인 성공 -> token 발행
        String token = JwtTokenUtil.createToken(user.getUserName(), secretKey, expiredTimeMs);

        return token;
    }

    // UserDetail
    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));
    }
}
