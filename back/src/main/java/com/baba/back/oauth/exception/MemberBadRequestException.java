package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class MemberBadRequestException extends BadRequestException {
    public MemberBadRequestException(String message) {
        super(message);
    }
}
