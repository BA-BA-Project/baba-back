package com.baba.back.oauth.exception;

import com.baba.back.exception.NotFoundException;

public class TokenNotFoundException extends NotFoundException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
