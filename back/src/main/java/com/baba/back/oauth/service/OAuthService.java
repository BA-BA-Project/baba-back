package com.baba.back.oauth.service;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private final OAuthClient oAuthClient;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final SignTokenProvider signTokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public SocialLoginResponse signInKakao(SocialTokenRequest request) {
        final String memberId = oAuthClient.getMemberId(request.getSocialToken());

        if (memberRepository.existsById(memberId)) {
            final String accessToken = accessTokenProvider.createToken(memberId);
            final String refreshToken = refreshTokenProvider.createToken(memberId);
            saveRefreshToken(memberId, refreshToken);
            return new SocialLoginResponse(HttpStatus.OK, new LoginTokenResponse(accessToken, refreshToken));
        }

        final String signToken = signTokenProvider.createToken(memberId);
        return new SocialLoginResponse(HttpStatus.NOT_FOUND, new SignTokenResponse(signToken));
    }

    private void saveRefreshToken(String memberId, String refreshToken) {
        tokenRepository.save(Token.builder()
                .id(memberId)
                .token(refreshToken)
                .build());
    }
}
