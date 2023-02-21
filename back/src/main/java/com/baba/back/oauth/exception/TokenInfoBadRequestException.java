package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class TokenInfoBadRequestException extends BadRequestException {
    public TokenInfoBadRequestException(String message) {
        super(message);
    }
}
