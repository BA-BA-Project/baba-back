package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class NameLengthBadRequestException extends BadRequestException {
    public NameLengthBadRequestException(String message) {
        super(message);
    }
}
