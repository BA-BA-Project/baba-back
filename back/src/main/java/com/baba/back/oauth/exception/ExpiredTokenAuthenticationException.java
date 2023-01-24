package com.baba.back.oauth.exception;

import com.baba.back.exception.AuthenticationException;

public class ExpiredTokenAuthenticationException extends AuthenticationException {
    public ExpiredTokenAuthenticationException(String message) {
        super(message);
    }
}
