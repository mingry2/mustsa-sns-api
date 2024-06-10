package com.mutsasns.controller;

import com.mutsasns.domain.dto.user.join.UserJoinRequest;
import com.mutsasns.domain.dto.user.join.UserJoinResponse;
import com.mutsasns.domain.dto.user.login.UserLoginRequest;
import com.mutsasns.domain.dto.user.login.UserLoginResponse;
import com.mutsasns.domain.dto.response.Response;
import com.mutsasns.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    @ApiOperation(
            value = "회원가입"
            , notes = "userName, password -> 회원가입")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest){
        log.debug("userName : {} password : {}  ", userJoinRequest.getUserName(), userJoinRequest.getPassword());
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest.getUserName(), userJoinRequest.getPassword());
        log.debug("userJoinResponse : {} ", userJoinResponse);
        return Response.success(userJoinResponse);
    }

    @ApiOperation(
            value = "로그인"
            , notes = "userName, password -> 로그인 -> jwt token 발급")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
