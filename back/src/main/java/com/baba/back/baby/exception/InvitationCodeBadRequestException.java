package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class InvitationCodeBadRequestException extends BadRequestException {
    public InvitationCodeBadRequestException(String message) {
        super(message);
    }
}
