package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.repository.MemberRepository;
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
    private MemberTokenProvider memberTokenProvider;

    @Mock
    private SignTokenProvider signTokenProvider;

    @Mock
    private MemberRepository memberRepository;

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
    void 가입이_되어_있으면_멤버_토큰을_발급한다() {
        // given
        final String validToken = "validToken";
        final String memberId = "memberId";
        given(oAuthClient.getMemberId(any())).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(true);
        given(memberTokenProvider.createToken(memberId)).willReturn("memberToken");

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(validToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.tokenResponse()).isNotNull()
        );
    }

    @Test
    void 가입이_되어있지_않으면_회원가입_토큰을_발급한다() {
        // given
        final String notMemberToken = "not member token";
        final String memberId = "memberId";
        final String signToken = "signToken";
        given(oAuthClient.getMemberId(notMemberToken)).willReturn(memberId);
        given(memberRepository.existsById(memberId)).willReturn(false);
        given(signTokenProvider.createToken(memberId)).willReturn(signToken);

        // when
        final SocialLoginResponse response = oAuthService.signInKakao(new SocialTokenRequest(notMemberToken));

        // then
        assertAll(
                () -> assertThat(response.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.tokenResponse()).isNotNull()
        );
    }
}
