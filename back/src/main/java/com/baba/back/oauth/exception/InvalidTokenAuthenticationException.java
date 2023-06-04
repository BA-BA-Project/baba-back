package com.baba.back.oauth.exception;

import com.baba.back.exception.AuthenticationException;

public class InvalidTokenAuthenticationException extends AuthenticationException {
    public InvalidTokenAuthenticationException(String message) {
        super(message);
    }
}
