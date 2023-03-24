package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class InvitationBadRequestException extends BadRequestException {
    public InvitationBadRequestException(String message) {
        super(message);
    }
}
