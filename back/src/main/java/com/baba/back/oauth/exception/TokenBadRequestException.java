package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class TokenBadRequestException extends BadRequestException {
    public TokenBadRequestException(String message) {
        super(message);
    }
}
