package com.baba.back.oauth.exception;

import com.baba.back.exception.NotFoundException;

public class JoinedMemberNotFoundException extends NotFoundException {
    public JoinedMemberNotFoundException(String message) {
        super(message);
    }
}
