package com.baba.back.oauth.service;

import com.baba.back.oauth.OAuthClient;
import com.baba.back.oauth.domain.Terms;
import com.baba.back.oauth.domain.member.Member;
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
import com.baba.back.oauth.exception.MemberBadRequestException;
import com.baba.back.oauth.exception.MemberNotFoundException;
import com.baba.back.oauth.exception.TermsBadRequestException;
import com.baba.back.oauth.exception.TokenBadRequestException;
import com.baba.back.oauth.repository.MemberRepository;
import com.baba.back.oauth.repository.TokenRepository;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private static final SearchTermsResponse SEARCH_TERMS_RESPONSE = new SearchTermsResponse(
            Arrays.stream(Terms.values())
                    .map(terms -> new TermsResponse(terms.isRequired(), terms.getName(), terms.getUrl()))
                    .toList());

    private final OAuthClient oAuthClient;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final SignTokenProvider signTokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public SocialLoginResponse signInKakao(SocialTokenRequest request) {
        final String memberId = oAuthClient.getMemberId(request.getSocialToken());
        final Member member = findMember(memberId);

        final String accessToken = accessTokenProvider.createToken(memberId);
        final String refreshToken = refreshTokenProvider.createToken(memberId);
        saveRefreshToken(member, refreshToken);

        return new SocialLoginResponse(accessToken, refreshToken);
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        final Token token = tokenRepository.findByMember(member)
                .orElseGet(() -> Token.builder().member(member).value(refreshToken).build());
        token.update(refreshToken);
        tokenRepository.save(token);
    }

    public SearchTermsResponse searchTerms(SocialTokenRequest request) {
        final String memberId = oAuthClient.getMemberId(request.getSocialToken());
        validateMember(memberId);

        return SEARCH_TERMS_RESPONSE;
    }

    private void validateMember(String memberId) {
        if (memberRepository.existsById(memberId)) {
            throw new MemberBadRequestException("이미 회원가입된 유저는 약관을 조회하거나 약관을 동의할 수 없습니다.");
        }
    }

    public SignTokenResponse agreeTerms(AgreeTermsRequest request) {
        final String memberId = oAuthClient.getMemberId(request.getSocialToken());
        validateMember(memberId);

        final List<TermsRequest> requestTerms = request.getTerms();
        final Set<Terms> selectedTerms = findValidTerms(requestTerms);
        validateDuplicate(requestTerms.size(), selectedTerms.size());
        validateSelectedTerms(selectedTerms.size());

        final String signToken = signTokenProvider.createToken(memberId);

        return new SignTokenResponse(signToken);
    }

    private Set<Terms> findValidTerms(List<TermsRequest> requestTerms) {
        return requestTerms
                .stream()
                .map(termsRequest -> {
                    final Terms terms = Terms.findByName(termsRequest.getName());
                    validateRequiredTerms(terms.isRequired(), termsRequest.isSelected());

                    return terms;
                })
                .collect(Collectors.toSet());
    }

    private void validateRequiredTerms(boolean isRequired, boolean isSelected) {
        if (isRequired && !isSelected) {
            throw new TermsBadRequestException("필수 동의 약관을 동의하지 않았습니다.");
        }
    }

    private void validateDuplicate(int beforeSize, int afterSize) {
        if (beforeSize != afterSize) {
            throw new TermsBadRequestException("중복된 약관이 존재합니다.");
        }
    }

    private void validateSelectedTerms(int size) {
        if (!Terms.isSizeEqualToAllTerms(size)) {
            throw new TermsBadRequestException("요청받은 필수 약관의 개수가 존재하는 필수 약관의 개수와 다릅니다.");
        }
    }

    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        final String oldRefreshToken = request.getRefreshToken();
        refreshTokenProvider.validateToken(oldRefreshToken);

        final String memberId = refreshTokenProvider.parseToken(oldRefreshToken);
        final Member member = findMember(memberId);

        validateToken(member, oldRefreshToken);

        final String newAccessToken = accessTokenProvider.createToken(memberId);

        if (refreshTokenProvider.isExpiringSoon(oldRefreshToken)) {
            final String newRefreshToken = refreshTokenProvider.createToken(memberId);
            saveRefreshToken(member, newRefreshToken);
            return new TokenRefreshResponse(newAccessToken, newRefreshToken);
        }

        return new TokenRefreshResponse(newAccessToken, oldRefreshToken);
    }

    private Member findMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId + "에 해당하는 멤버가 존재하지 않습니다."));
    }

    private void validateToken(Member member, String refreshToken) {
        if (!tokenRepository.existsByMemberAndValue(member, refreshToken)) {
            throw new TokenBadRequestException(refreshToken + "는 DB에 저장된 토큰과 일치하지 않습니다.");
        }
    }
}
