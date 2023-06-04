package com.baba.back.oauth.controller;

import com.baba.back.oauth.service.SignTokenProvider;
import com.baba.back.oauth.support.AuthorizationExtractor;
import com.baba.back.oauth.support.SignUp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class SignUpArgumentResolver implements HandlerMethodArgumentResolver {

    private final SignTokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasSignUpAnnotation = parameter.hasParameterAnnotation(SignUp.class);
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());

        return hasSignUpAnnotation && hasStringType;
    }

    @Override
    public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String token = AuthorizationExtractor.extractOrThrow(request);

        return tokenProvider.parseToken(token);
    }
}
