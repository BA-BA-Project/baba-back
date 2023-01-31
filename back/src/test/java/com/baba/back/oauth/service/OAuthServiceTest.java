package com.baba.back.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        final String code = "code";
        final String accessToken = "accessToken";
        final String memberId = "memberId";
        final String memberToken = "member token";

        when(oAuthClient.getOAuthAccessToken(code)).thenReturn(new OAuthAccessTokenResponse(accessToken));
        when(oAuthClient.getMemberId(accessToken)).thenReturn(memberId);
        when(joinedMemberRepository.findById(memberId)).thenReturn(Optional.empty());
        when(tokenProvider.createToken(memberId)).thenReturn(memberToken);
        when(joinedMemberRepository.save(any())).thenReturn(new JoinedMember(memberId, false));

        // when & then
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
        final String code = "code";
        final String accessToken = "accessToken";
        final String memberId = "memberId";
        final String memberToken = "member token";

        when(oAuthClient.getOAuthAccessToken(code)).thenReturn(new OAuthAccessTokenResponse(accessToken));
        when(oAuthClient.getMemberId(accessToken)).thenReturn(memberId);
        when(joinedMemberRepository.findById(memberId)).thenReturn(Optional.of(new JoinedMember(memberId, false)));
        when(tokenProvider.createToken(memberId)).thenReturn(memberToken);

        // when & then
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
        final String code = "code";
        final String accessToken = "accessToken";
        final String memberId = "memberId";
        final String memberToken = "member token";

        when(oAuthClient.getOAuthAccessToken(code)).thenReturn(new OAuthAccessTokenResponse(accessToken));
        when(oAuthClient.getMemberId(accessToken)).thenReturn(memberId);
        when(joinedMemberRepository.findById(memberId)).thenReturn(Optional.of(new JoinedMember(memberId, true)));
        when(tokenProvider.createToken(memberId)).thenReturn(memberToken);

        // when & then
        final TokenResponse tokenResponse = oAuthService.signInKakao(code);

        // then
        assertAll(
                () -> assertThat(tokenResponse.getSignedUp()).isTrue(),
                () -> assertThat(tokenResponse.getMessage()).isNotBlank(),
                () -> assertThat(tokenResponse.getToken()).isNotBlank()
        );
    }
}
