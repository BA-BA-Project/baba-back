package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class InvitationsBadRequestException extends BadRequestException {
    public InvitationsBadRequestException(String message) {
        super(message);
    }
}
