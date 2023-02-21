package com.baba.back.oauth.service;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.dto.SocialLoginResponse;
import com.baba.back.oauth.dto.SocialTokenRequest;
import com.baba.back.oauth.dto.TokenResponse;
import com.baba.back.oauth.repository.MemberRepository;
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
    private final MemberTokenProvider memberTokenProvider;
    private final MemberRepository memberRepository;

    public SocialLoginResponse signInKakao(SocialTokenRequest request) {
        final String memberId = oAuthClient.getMemberId(request.getSocialToken());
        final String memberToken = memberTokenProvider.createToken(memberId);

        if (memberRepository.existsById(memberId)) {
            return new SocialLoginResponse(HttpStatus.OK, new TokenResponse(memberToken));
        }

        return new SocialLoginResponse(HttpStatus.NOT_FOUND, new TokenResponse(memberToken));
    }
}
