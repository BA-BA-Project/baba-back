package com.baba.back.oauth;

import com.baba.back.oauth.dto.OAuthAccessTokenResponse;

public interface OAuthClient {

    OAuthAccessTokenResponse getOAuthAccessToken(String code);

    String getUserId(String accessToken);
}
