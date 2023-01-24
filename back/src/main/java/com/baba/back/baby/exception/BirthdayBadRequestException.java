package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class BirthdayBadRequestException extends BadRequestException {
    public BirthdayBadRequestException(String message) {
        super(message);
    }
}
