package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    @InjectMocks
    private OAuthService oAuthService;

    @Mock
    private OAuthClient oAuthClient;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    void 소셜_토큰이_유효하지_않다면_400을_반환한다() {
        // given
        final String invalidToken = "invalidToken";
        given(oAuthClient.getMemberId(invalidToken)).willThrow(HttpClientErrorException.class);

        // when & then
        final SocialTokenRequest request = new SocialTokenRequest(invalidToken);

        assertThatThrownBy(() -> oAuthService.signInKakao(request))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void 가입이_되어_있으면_200을_응답한다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        given(oAuthClient.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(tokenRepository.save(any())).willReturn(any());
        given(accessTokenProvider.createToken(memberId)).willReturn("accessToken");
        given(refreshTokenProvider.createToken(memberId)).willReturn("refreshToken");

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.tokenResponse().accessToken()).isNotBlank(),
                () -> assertThat(response.tokenResponse().refreshToken()).isNotBlank()
        );

        then(tokenRepository).should(times(1)).save(any());
    }

    @Test
    void 가입이_되어있지_않으면_404를_응답한다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        given(oAuthClient.getMemberId(validToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(false);
        given(tokenRepository.save(any())).willReturn(any());
        given(accessTokenProvider.createToken(memberId)).willReturn("accessToken");
        given(refreshTokenProvider.createToken(memberId)).willReturn("refreshToken");

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.tokenResponse().accessToken()).isNotBlank(),
                () -> assertThat(response.tokenResponse().refreshToken()).isNotBlank()
        );

        then(tokenRepository).should(times(1)).save(any());
    }
}
