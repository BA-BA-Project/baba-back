package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class TermsBadRequestException extends BadRequestException {
    public TermsBadRequestException(String message) {
        super(message);
    }
}
