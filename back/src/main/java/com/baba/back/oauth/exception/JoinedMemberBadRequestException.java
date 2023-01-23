package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class JoinedMemberBadRequestException extends BadRequestException {
    public JoinedMemberBadRequestException(String message) {
        super(message);
    }
}
