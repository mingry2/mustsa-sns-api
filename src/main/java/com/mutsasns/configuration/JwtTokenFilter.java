package com.mutsasns.configuration;

import com.mutsasns.domain.entity.User;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.UserService;
import com.mutsasns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, RuntimeException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization: {}", authorization);

        // token이 넘어오지 않았을 때,
        if (authorization == null || !authorization.startsWith("Bearer ")){
            request.setAttribute("invalidTokenException", ErrorCode.INVALID_TOKEN);
            filterChain.doFilter(request, response);
            return;
        }

        // token 꺼내기
        String token = authorization.split(" ")[1];

        // token 만료 여부 체크
        if (JwtTokenUtil.isExpired(token, secretKey)){
            log.error("token 사용시간이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // userName token에서 꺼내기
        String userName = JwtTokenUtil.getUserName(token, secretKey);
        log.info("userName : {}", userName);

        // UserDetail
        User user = userService.getUserByUserName(userName);
        log.debug("userRole: {}", user.getRole());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);

    }
}
