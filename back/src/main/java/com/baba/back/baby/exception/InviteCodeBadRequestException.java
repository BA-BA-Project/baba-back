package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class InviteCodeBadRequestException extends BadRequestException {
    public InviteCodeBadRequestException(String message) {
        super(message);
    }
}
