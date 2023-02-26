package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
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
    private SignTokenProvider signTokenProvider;

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
        assertThatThrownBy(() -> oAuthService.signInKakao(new SocialTokenRequest(invalidToken)))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void 가입이_되어_있으면_access토큰과_refresh토큰을_발급한다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        given(oAuthClient.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(accessTokenProvider.createToken(memberId)).willReturn(accessToken);
        given(refreshTokenProvider.createToken(memberId)).willReturn(refreshToken);
        given(tokenRepository.save(any(Token.class))).willReturn(any());

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.tokenResponse()).isEqualTo(new LoginTokenResponse(accessToken, refreshToken))
        );

        then(tokenRepository).should(times(1)).save(any());
    }

    @Test
    void 가입이_되어있지_않으면_sign토큰을_발급한다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        final String signToken = "signToken";
        given(oAuthClient.getMemberId(validToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(false);
        given(signTokenProvider.createToken(memberId)).willReturn(signToken);

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.tokenResponse()).isEqualTo(new SignTokenResponse(signToken))
        );
    }
}
