package com.baba.back.oauth;

import com.baba.back.oauth.dto.OAuthAccessTokenResponse;

public class FakeOAuthClient implements OAuthClient {

    @Override
    public OAuthAccessTokenResponse getOAuthAccessToken(final String code) {
        return new OAuthAccessTokenResponse("kakako_access_token");
    }

    @Override
    public String getMemberId(final String accessToken) {
        return "KAKAO" + accessToken;
    }
}
