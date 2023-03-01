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

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", swaggerPath, "/swagger-ui/**", "/v3/api-docs/**", "/members/baby");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
    }
}
