package com.baba.back.oauth.service;

import static com.baba.back.fixture.DomainFixture.멤버1;
import static com.baba.back.fixture.DomainFixture.토큰;
import static com.baba.back.fixture.RequestFixture.약관_동의_요청_데이터;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.Terms;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.AgreeTermsRequest;
import com.baba.back.oauth.dto.SearchTermsResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TermsRequest;
import com.baba.back.oauth.dto.TermsResponse;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.exception.ExpiredTokenAuthenticationException;
import com.baba.back.oauth.exception.InvalidTokenAuthenticationException;
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.exception.TermsBadRequestException;
import com.baba.back.oauth.exception.TokenBadRequestException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void 소셜_토큰이_유효하지_않다면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        given(oAuthClient.getMemberId(invalidToken)).willThrow(HttpClientErrorException.class);

        // when & then
        final SocialTokenRequest request = new SocialTokenRequest(invalidToken);

        assertThatThrownBy(() -> oAuthService.signInKakao(request))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void 가입이_되어있지_않으면_예외를_던진다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        final SocialTokenRequest request = new SocialTokenRequest(validToken);

        given(oAuthClient.getMemberId(validToken)).willReturn(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> oAuthService.signInKakao(request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 소셜_로그인시_가입이_되어_있고_refresh토큰이_이미_저장되어있다면_access토큰과_refresh토큰을_발급한다() {
        // given
        final String validToken = "validToken";
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(accessTokenProvider.createToken(멤버1.getId())).willReturn(accessToken);
        given(refreshTokenProvider.createToken(멤버1.getId())).willReturn(refreshToken);
        given(tokenRepository.findByMember(멤버1)).willReturn(Optional.of(new Token(멤버1, refreshToken)));

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.accessToken()).isEqualTo(accessToken),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );

        then(tokenRepository).should(times(1)).save(any());
    }

    @Test
    void 소셜_로그인시_가입이_되어_있고_저장된_refresh토큰이_없어도_access토큰과_refresh토큰을_발급한다() {
        // given
        final String validToken = "validToken";
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        given(oAuthClient.getMemberId(validToken)).willReturn(멤버1.getId());
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(accessTokenProvider.createToken(멤버1.getId())).willReturn(accessToken);
        given(refreshTokenProvider.createToken(멤버1.getId())).willReturn(refreshToken);
        given(tokenRepository.findByMember(멤버1)).willReturn(Optional.empty());

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.accessToken()).isEqualTo(accessToken),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );

        then(tokenRepository).should(times(1)).save(any());
    }

    @Test
    void 약관_조회_요청시_소셜_토큰이_유효하지_않다면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final SocialTokenRequest request = new SocialTokenRequest(invalidToken);
        given(oAuthClient.getMemberId(invalidToken)).willThrow(HttpClientErrorException.class);

        // when & then
        assertThatThrownBy(() -> oAuthService.searchTerms(request))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void 약관_조회_요청시_이미_가입_되어있으면_예외를_던진다() {
        // given
        final String validToken = "validToken";
        final SocialTokenRequest request = new SocialTokenRequest(validToken);
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> oAuthService.searchTerms(request))
                .isInstanceOf(MemberBadRequestException.class);
    }

    @Test
    void 약관_조회_요청시_약관을_응답한다() {
        // given
        final String validToken = "invalidToken";
        final SocialTokenRequest request = new SocialTokenRequest(validToken);
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(false);

        // when
        final SearchTermsResponse response = oAuthService.searchTerms(request);

        // then
        assertThat(response.terms()).containsExactly(
                new TermsResponse(Terms.TERMS_1.isRequired(), Terms.TERMS_1.getName(), Terms.TERMS_1.getUrl()),
                new TermsResponse(Terms.TERMS_2.isRequired(), Terms.TERMS_2.getName(), Terms.TERMS_2.getUrl())
        );
    }

    @Test
    void 약관_동의_요청시_소셜_토큰이_유효하지_않다면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final AgreeTermsRequest request = new AgreeTermsRequest(
                invalidToken, List.of(new TermsRequest("이용약관 동의", true)));
        given(oAuthClient.getMemberId(invalidToken)).willThrow(HttpClientErrorException.class);

        // when & then
        assertThatThrownBy(() -> oAuthService.agreeTerms(request))
                .isInstanceOf(HttpClientErrorException.class);
    }

    @Test
    void 약관_동의_요청시_이미_가입_되어있으면_예외를_던진다() {
        // given
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> oAuthService.agreeTerms(약관_동의_요청_데이터))
                .isInstanceOf(MemberBadRequestException.class);
    }

    @Test
    void 약관_동의_요청시_요청받은_약관의_개수와_존재하는_약관의_개수가_다르다면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final AgreeTermsRequest request = new AgreeTermsRequest(
                invalidToken, List.of(new TermsRequest(Terms.TERMS_1.getName(), true)));
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> oAuthService.agreeTerms(request))
                .isInstanceOf(TermsBadRequestException.class);
    }

    @Test
    void 약관_동의_요청시_잘못된_약관이_존재하면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final AgreeTermsRequest request = new AgreeTermsRequest(invalidToken,
                List.of(new TermsRequest(Terms.TERMS_1.getName(), true),
                        new TermsRequest(Terms.TERMS_1.getName(), true)));
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> oAuthService.agreeTerms(request))
                .isInstanceOf(TermsBadRequestException.class);
    }

    @Test
    void 약관_동의_요청시_모든_필수_동의_약관을_동의하지_않았으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final AgreeTermsRequest request = new AgreeTermsRequest(invalidToken,
                List.of(new TermsRequest(Terms.TERMS_1.getName(), true),
                        new TermsRequest(Terms.TERMS_2.getName(), false)));
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> oAuthService.agreeTerms(request))
                .isInstanceOf(TermsBadRequestException.class);
    }

    @Test
    void 약관_동의_요청시_모든_필수_동의_약관을_동의하면_signToken을_발급한다() {
        // given
        final String signToken = "signToken";
        given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
        given(memberRepository.existsById(멤버1.getId())).willReturn(false);
        given(signTokenProvider.createToken(멤버1.getId())).willReturn(signToken);

        // when
        final SignTokenResponse response = oAuthService.agreeTerms(약관_동의_요청_데이터);

        // then
        assertThat(response.signToken()).isEqualTo(signToken);
    }

    @Test
    void 약관_동의_요청시_선택_동의_약관은_동의하지_않아도_signToken을_발급한다() {
        final Terms[] values = Terms.values();
        try (MockedStatic<Terms> mockedTerms = mockStatic(Terms.class)) {
            // given
            final String signToken = "signToken";
            given(oAuthClient.getMemberId(any())).willReturn(멤버1.getId());
            given(memberRepository.existsById(멤버1.getId())).willReturn(false);
            mockedTerms.when(Terms::values).thenReturn(values);
            mockedTerms.when(() -> Terms.isSameSize(values.length)).thenReturn(true);
            mockedTerms.when(() -> Terms.isRequiredTermsBy(0, Terms.TERMS_1.getName())).thenReturn(true);
            mockedTerms.when(() -> Terms.isRequiredTermsBy(1, Terms.TERMS_2.getName())).thenReturn(false);
            given(signTokenProvider.createToken(멤버1.getId())).willReturn(signToken);

            // when
            final SignTokenResponse response = oAuthService.agreeTerms(약관_동의_요청_데이터);

            // then
            assertThat(response.signToken()).isEqualTo(signToken);
        }
    }

    @Test
    void 토큰재발급시_토큰이_만료됐으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        willThrow(ExpiredTokenAuthenticationException.class).given(refreshTokenProvider).validateToken(invalidToken);

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(ExpiredTokenAuthenticationException.class);
    }

    @Test
    void 토큰재발급시_토큰이_유효하지_않으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        willThrow(InvalidTokenAuthenticationException.class).given(refreshTokenProvider).validateToken(invalidToken);

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(InvalidTokenAuthenticationException.class);
    }

    @Test
    void 토큰재발급시_멤버가_없으면_예외를_던진다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";

        willDoNothing().given(refreshTokenProvider).validateToken(validToken);
        given(refreshTokenProvider.parseToken(validToken)).willReturn(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(validToken)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 토큰재발급시_DB에_토큰이_없거나_토큰이_일치하지_않으면_예외를_던진다() {
        // given
        final String invalidToken = "invalidToken";
        final String memberId = "memberId";

        willDoNothing().given(refreshTokenProvider).validateToken(invalidToken);
        given(refreshTokenProvider.parseToken(invalidToken)).willReturn(멤버1.getId());
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(tokenRepository.existsByMemberAndValue(토큰.getMember(), invalidToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> oAuthService.refresh(new TokenRefreshRequest(invalidToken)))
                .isInstanceOf(TokenBadRequestException.class);
    }

    @Test
    void 토큰재발급시_토큰의_만료기간이_하루보다_많이_남았으면_access토큰만_재발급한다() {
        // given
        final String accessToken = "accessToken";

        willDoNothing().given(refreshTokenProvider).validateToken(토큰.getValue());
        given(refreshTokenProvider.parseToken(토큰.getValue())).willReturn(멤버1.getId());
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(tokenRepository.existsByMemberAndValue(토큰.getMember(), 토큰.getValue())).willReturn(true);
        given(accessTokenProvider.createToken(멤버1.getId())).willReturn(accessToken);
        given(refreshTokenProvider.isExpiringSoon(토큰.getValue())).willReturn(false);

        // when
        final TokenRefreshResponse response = oAuthService.refresh(new TokenRefreshRequest(토큰.getValue()));

        // then
        Assertions.assertAll(
                () -> assertThat(response.accessToken()).isNotBlank(),
                () -> assertThat(response.refreshToken()).isEqualTo(토큰.getValue())
        );
    }

    @Test
    void 토큰재발급시_토큰의_만료기간이_하루_이하로_남았으면_access토큰과_refresh토큰을_재발급한다() {
        // given
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        willDoNothing().given(refreshTokenProvider).validateToken(토큰.getValue());
        given(refreshTokenProvider.parseToken(토큰.getValue())).willReturn(멤버1.getId());
        given(memberRepository.findById(멤버1.getId())).willReturn(Optional.of(멤버1));
        given(tokenRepository.existsByMemberAndValue(토큰.getMember(), 토큰.getValue())).willReturn(true);
        given(accessTokenProvider.createToken(멤버1.getId())).willReturn(accessToken);
        given(refreshTokenProvider.isExpiringSoon(토큰.getValue())).willReturn(true);
        given(refreshTokenProvider.createToken(멤버1.getId())).willReturn(refreshToken);

        // when
        final TokenRefreshResponse response = oAuthService.refresh(new TokenRefreshRequest(토큰.getValue()));

        // then
        Assertions.assertAll(
                () -> assertThat(response.accessToken()).isNotBlank(),
                () -> assertThat(response.refreshToken()).isEqualTo(refreshToken)
        );
    }
}
