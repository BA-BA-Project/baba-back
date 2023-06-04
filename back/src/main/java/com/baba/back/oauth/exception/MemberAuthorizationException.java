package com.baba.back.oauth.exception;

import com.baba.back.exception.AuthorizationException;

public class MemberAuthorizationException extends AuthorizationException {
    public MemberAuthorizationException(String message) {
        super(message);
    }
}
