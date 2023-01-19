package com.baba.back.auth.service;

import com.baba.back.auth.dto.AuthAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

    public void signInKakao(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = "602ed6d3f3ca0443eaeba3e769086fa0";
        String grantType = "authorization_code";
        String redirectUri = "http://localhost:8080/login/oauth2/code/kakao";

        HttpHeaders headers  = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 실제요청
        ResponseEntity<AuthAccessTokenResponse> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                AuthAccessTokenResponse.class
        );

        String accessToken = response.getBody().getAccessToken();


        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers2);

        String body = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class
        ).getBody();

        log.info(body);
    }


}
