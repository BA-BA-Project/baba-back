package com.baba.back.baby.invitation.exception;

import com.baba.back.exception.BadRequestException;

public class InviteCodeBadRequestException extends BadRequestException {
    public InviteCodeBadRequestException(String message) {
        super(message);
    }
}
