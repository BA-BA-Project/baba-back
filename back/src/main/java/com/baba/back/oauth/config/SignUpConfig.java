package com.baba.back.oauth.config;

import com.baba.back.oauth.controller.SignUpArgumentResolver;
import com.baba.back.oauth.controller.SignUpInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SignUpConfig implements WebMvcConfigurer {

    private final SignUpInterceptor signUpInterceptor;
    private final SignUpArgumentResolver signUpArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signUpInterceptor)
                .addPathPatterns("/members/baby/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(signUpArgumentResolver);
    }
}