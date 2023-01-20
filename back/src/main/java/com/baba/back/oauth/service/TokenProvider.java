package com.baba.back.oauth.service;

public interface TokenProvider {
    String createToken(String payload);
}
