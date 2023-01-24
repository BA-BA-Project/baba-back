package com.baba.back.oauth.service;

public interface TokenProvider {
    String createToken(String payload);

    void validateToken(String token);

    String parseToken(String token);
}
