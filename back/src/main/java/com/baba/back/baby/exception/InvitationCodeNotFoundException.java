package com.baba.back.baby.exception;

import com.baba.back.exception.NotFoundException;

public class InvitationCodeNotFoundException extends NotFoundException {

    public InvitationCodeNotFoundException(String message) {
        super(message);
    }
}
