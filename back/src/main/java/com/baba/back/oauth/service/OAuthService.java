package com.baba.back.oauth.service;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.member.Member;
import com.baba.back.oauth.domain.token.Token;
import com.baba.back.oauth.dto.LoginTokenResponse;
import com.baba.back.oauth.dto.SignTokenResponse;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenRefreshRequest;
import com.baba.back.oauth.dto.TokenRefreshResponse;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.exception.TokenBadRequestException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
        final Optional<Member> member = memberRepository.findById(memberId);

        if (member.isPresent()) {
            final String accessToken = accessTokenProvider.createToken(memberId);
            final String refreshToken = refreshTokenProvider.createToken(memberId);
            saveRefreshToken(member.get(), refreshToken);
            return new SocialLoginResponse(HttpStatus.OK, new LoginTokenResponse(accessToken, refreshToken));
        }

        final String signToken = signTokenProvider.createToken(memberId);
        return new SocialLoginResponse(HttpStatus.NOT_FOUND, new SignTokenResponse(signToken));
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        tokenRepository.save(Token.builder()
                .member(member)
                .value(refreshToken)
                .build());
    }

    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        final String refreshToken = request.getRefreshToken();
        refreshTokenProvider.validateToken(refreshToken);

        final String memberId = refreshTokenProvider.parseToken(refreshToken);
        final Member member = findMember(memberId);

        validateEqualToken(member, refreshToken);

        final String newAccessToken = accessTokenProvider.createToken(memberId);

        if (refreshTokenProvider.isExpiringSoon(refreshToken)) {
            final String newRefreshToken = refreshTokenProvider.createToken(memberId);
            return new TokenRefreshResponse(newAccessToken, newRefreshToken);
        }

        return new TokenRefreshResponse(newAccessToken, refreshToken);
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다.")
        );
    }

    private void validateEqualToken(Member member, String refreshToken) {
        if (!tokenRepository.existsByMemberAndValue(member, refreshToken)) {
            throw new TokenBadRequestException(refreshToken + "는 DB에 저장된 토큰과 일치하지 않습니다.");
        }
    }
}
