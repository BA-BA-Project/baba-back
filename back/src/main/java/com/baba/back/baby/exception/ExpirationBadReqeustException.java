package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class ExpirationBadReqeustException extends BadRequestException {
    public ExpirationBadReqeustException(String message) {
        super(message);
    }
}
