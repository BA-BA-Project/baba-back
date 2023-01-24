package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.baba.back.oauth.TestConfig;
import com.baba.back.oauth.dto.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;

    @Test
    void 이미_가입되어있는_유저인지_확인한다() {
        // given
        String code = "code";

        // when
        final TokenResponse tokenResponse = oAuthService.signInKakao(code);

        // then
        assertAll(
                () -> assertThat(tokenResponse.getSignedUp()).isFalse(),
                () -> assertThat(tokenResponse.getMessage()).isNotBlank(),
                () -> assertThat(tokenResponse.getToken()).isNotBlank()
        );
    }
}
