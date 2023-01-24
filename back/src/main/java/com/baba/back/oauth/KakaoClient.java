package com.baba.back.oauth;

import com.baba.back.oauth.dto.OAuthAccessTokenResponse;
import com.baba.back.oauth.dto.OAuthIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoClient implements OAuthClient {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    @Value("${security.oauth.kakao.grant-type}")
    private String grantType;

    @Value("${security.oauth.kakao.client-id}")
    private String clientId;

    @Value("${security.oauth.kakao.redirect-uri}")
    private String redirectUri;


    @Override
    public OAuthAccessTokenResponse getOAuthAccessToken(final String code) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, httpHeaders);

        // 실제요청
        return REST_TEMPLATE.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                OAuthAccessTokenResponse.class
        ).getBody();
    }

    @Override
    public String getMemberId(final String accessToken) {
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers2);

        final OAuthIdResponse response = REST_TEMPLATE.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                OAuthIdResponse.class
        ).getBody();
        return "KAKAO" + response.getId();
    }
}
