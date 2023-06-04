package com.baba.back.oauth;

import com.baba.back.oauth.dto.OAuthIdResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoClient implements OAuthClient {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

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
