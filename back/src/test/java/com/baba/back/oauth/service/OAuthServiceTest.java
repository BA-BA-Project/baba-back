package com.baba.back.oauth.service;

import static com.baba.back.fixture.DomainFixture.토큰;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.exception.ExpiredTokenAuthenticationException;
import com.baba.back.oauth.exception.InvalidTokenAuthenticationException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.exception.TokenBadRequestException;
import com.baba.back.oauth.exception.TokenNotFoundException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
        final SocialTokenRequest request = new SocialTokenRequest(invalidToken);

        assertThatThrownBy(() -> oAuthService.signInKakao(request))
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

    @Test
    void 토큰재발급시_토큰이_만료됐으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        willThrow(ExpiredTokenAuthenticationException.class).given(refreshTokenProvider).validateToken(any());

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(ExpiredTokenAuthenticationException.class);
    }

    @Test
    void 토큰재발급시_토큰이_유효하지_않으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        willThrow(InvalidTokenAuthenticationException.class).given(refreshTokenProvider).validateToken(any());

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(InvalidTokenAuthenticationException.class);
    }

    @Test
    void 토큰재발급시_멤버가_없으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final String memberId = "memberId";

        willDoNothing().given(refreshTokenProvider).validateToken(any());
        given(refreshTokenProvider.parseToken(invalidToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 토큰재발급시_DB에_토큰이_없으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final String memberId = "memberId";

        willDoNothing().given(refreshTokenProvider).validateToken(any());
        given(refreshTokenProvider.parseToken(invalidToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(tokenRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    void 토큰재발급시_토큰이_일치하지_않으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final String memberId = "memberId";

        willDoNothing().given(refreshTokenProvider).validateToken(any());
        given(refreshTokenProvider.parseToken(invalidToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(tokenRepository.findById(memberId)).willReturn(Optional.of(토큰));

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(TokenBadRequestException.class);
    }

    @Test
    void 토큰재발급시_토큰의_만료기간이_하루보다_많이_남았으면_access토큰만_재발급한다() {
        // given
        final String accessToken = "accessToken";

        willDoNothing().given(refreshTokenProvider).validateToken(any());
        given(refreshTokenProvider.parseToken(토큰.getToken())).willReturn(토큰.getId());
        given(memberRepository.existsById(토큰.getId())).willReturn(true);
        given(tokenRepository.findById(토큰.getId())).willReturn(Optional.of(토큰));
        given(accessTokenProvider.createToken(토큰.getId())).willReturn(accessToken);
        given(refreshTokenProvider.checkExpiration(토큰.getToken())).willReturn(false);

        // when
        final TokenRefreshResponse response = oAuthService.refresh(new TokenRefreshRequest(토큰.getToken()));

        // then
        Assertions.assertAll(
                () -> assertThat(response.accessToken()).isNotBlank(),
                () -> assertThat(response.refreshToken()).isEqualTo(토큰.getToken())
        );
    }

    @Test
    void 토큰재발급시_토큰의_만료기간이_하루_이하로_남았으면_access토큰과_refresh토큰을_재발급한다() {
        // given
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        willDoNothing().given(refreshTokenProvider).validateToken(any());
        given(refreshTokenProvider.parseToken(토큰.getToken())).willReturn(토큰.getId());
        given(memberRepository.existsById(토큰.getId())).willReturn(true);
        given(tokenRepository.findById(토큰.getId())).willReturn(Optional.of(토큰));
        given(accessTokenProvider.createToken(토큰.getId())).willReturn(accessToken);
        given(refreshTokenProvider.checkExpiration(토큰.getToken())).willReturn(true);
        given(refreshTokenProvider.createToken(토큰.getId())).willReturn(refreshToken);

        // when
        final TokenRefreshResponse response = oAuthService.refresh(new TokenRefreshRequest(토큰.getToken()));

        // then
        Assertions.assertAll(
                () -> assertThat(response.accessToken()).isNotBlank(),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );
    }
}
