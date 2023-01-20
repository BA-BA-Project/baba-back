package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class IntroductionLengthBadRequestException extends BadRequestException {
    public IntroductionLengthBadRequestException(String message) {
        super(message);
    }
}
