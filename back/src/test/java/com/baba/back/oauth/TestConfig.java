package com.baba.back.oauth;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public OAuthClient oauthClient() {
        return new FakeOAuthClient();
    }
}
