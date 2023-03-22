package com.baba.back.baby.exception;

import com.baba.back.exception.NotFoundException;

public class InvitationNotFoundException extends NotFoundException {
    public InvitationNotFoundException(String message) {
        super(message);
    }
}
