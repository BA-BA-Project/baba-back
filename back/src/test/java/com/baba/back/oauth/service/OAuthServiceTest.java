package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.JoinedMember;
import com.baba.back.oauth.dto.OAuthAccessTokenResponse;
import com.baba.back.oauth.dto.TokenResponse;
import com.baba.back.oauth.repository.JoinedMemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthServiceTest {

    final String code = "code";
    final String accessToken = "accessToken";
    final String memberId = "memberId";
    final String memberToken = "member token";
    @InjectMocks
    private OAuthService oAuthService;
    @Mock
    private OAuthClient oAuthClient;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private JoinedMemberRepository joinedMemberRepository;

    @Test
    void 첫_로그인_시_토큰을_발급한다() {
        // given
        given(oAuthClient.getOAuthAccessToken(code)).willReturn(new OAuthAccessTokenResponse(accessToken));
        given(oAuthClient.getMemberId(accessToken)).willReturn(memberId);
        given(joinedMemberRepository.findById(memberId)).willReturn(Optional.empty());
        given(tokenProvider.createToken(memberId)).willReturn(memberToken);
        given(joinedMemberRepository.save(any())).willReturn(new JoinedMember(memberId, false));

        // when
        final TokenResponse tokenResponse = oAuthService.signInKakao(code);

        // then
        assertAll(
                () -> assertThat(tokenResponse.getSignedUp()).isFalse(),
                () -> assertThat(tokenResponse.getMessage()).isNotBlank(),
                () -> assertThat(tokenResponse.getToken()).isNotBlank()
        );
    }

    @Test
    void 첫_로그인을_했지만_가입하지않은_멤버에게_토큰을_발급한다() {
        // given
        given(oAuthClient.getOAuthAccessToken(code)).willReturn(new OAuthAccessTokenResponse(accessToken));
        given(oAuthClient.getMemberId(accessToken)).willReturn(memberId);
        given(joinedMemberRepository.findById(memberId)).willReturn(Optional.of(new JoinedMember(memberId, false)));
        given(tokenProvider.createToken(memberId)).willReturn(memberToken);

        // when
        final TokenResponse tokenResponse = oAuthService.signInKakao(code);

        // then
        assertAll(
                () -> assertThat(tokenResponse.getSignedUp()).isFalse(),
                () -> assertThat(tokenResponse.getMessage()).isNotBlank(),
                () -> assertThat(tokenResponse.getToken()).isNotBlank()
        );
    }

    @Test
    void 이미_가입한_멤버에게_토큰을_발급한다() {
        // given
        given(oAuthClient.getOAuthAccessToken(code)).willReturn(new OAuthAccessTokenResponse(accessToken));
        given(oAuthClient.getMemberId(accessToken)).willReturn(memberId);
        given(joinedMemberRepository.findById(memberId)).willReturn(Optional.of(new JoinedMember(memberId, true)));
        given(tokenProvider.createToken(memberId)).willReturn(memberToken);

        // when
        final TokenResponse tokenResponse = oAuthService.signInKakao(code);

        // then
        assertAll(
                () -> assertThat(tokenResponse.getSignedUp()).isTrue(),
                () -> assertThat(tokenResponse.getMessage()).isNotBlank(),
                () -> assertThat(tokenResponse.getToken()).isNotBlank()
        );
    }
}
