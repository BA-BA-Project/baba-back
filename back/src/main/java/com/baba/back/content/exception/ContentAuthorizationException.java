package com.baba.back.content.exception;

import com.baba.back.exception.AuthorizationException;

public class ContentAuthorizationException extends AuthorizationException {
    public ContentAuthorizationException(String message) {
        super(message);
    }
}
