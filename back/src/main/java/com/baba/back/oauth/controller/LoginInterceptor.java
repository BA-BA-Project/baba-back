package com.baba.back.oauth.controller;

import com.baba.back.oauth.service.MemberTokenProvider;
import com.baba.back.oauth.support.AuthorizationExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final MemberTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = AuthorizationExtractor.extractOrThrow(request);
        tokenProvider.validateToken(token);
        return true;
    }
}
