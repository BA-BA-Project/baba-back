package com.baba.back.oauth.config;

import com.baba.back.oauth.controller.LoginArgumentResolver;
import com.baba.back.oauth.controller.LoginInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class LoginConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginArgumentResolver loginArgumentResolver;

    @Value("${server.servlet.context-path}")
    private String baseUrl;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(baseUrl + "/**")
                .excludePathPatterns(baseUrl + "/login/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
    }
}
