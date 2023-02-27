package com.baba.back.oauth.service;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.exception.TokenBadRequestException;
import com.baba.back.oauth.exception.TokenNotFoundException;
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

    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        final String refreshToken = request.getRefreshToken();
        refreshTokenProvider.validateToken(refreshToken);

        final String memberId = refreshTokenProvider.parseToken(refreshToken);
        validateMember(memberId);

        final Token token = findToken(memberId);
        validateEqualToken(refreshToken, token);

        final String newAccessToken = accessTokenProvider.createToken(memberId);

        if(refreshTokenProvider.checkExpiration(refreshToken)) {
            return new TokenRefreshResponse(newAccessToken, refreshToken);
        }

        final String newRefreshToken = refreshTokenProvider.createToken(memberId);
        return new TokenRefreshResponse(newAccessToken, newRefreshToken);
    }

    private void validateMember(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다.");
        }
    }

    private Token findToken(String memberId) {
        return tokenRepository.findById(memberId)
                .orElseThrow(() -> new TokenNotFoundException(memberId + "에 해당하는 refresh 토큰이 존재하지 않습니다."));
    }

    private static void validateEqualToken(String refreshToken, Token token) {
        if (!token.hasEqualToken(refreshToken)) {
            throw new TokenBadRequestException(refreshToken + "는 DB에 저장된 토큰과 일치하지 않습니다.");
        }
    }
}
