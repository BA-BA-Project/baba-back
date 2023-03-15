package com.baba.back.invitation.exception;

import com.baba.back.exception.NotFoundException;

public class RelationGroupNotFoundException extends NotFoundException {
    public RelationGroupNotFoundException(String message) {
        super(message);
    }
}
