package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class BabyBadRequestException extends BadRequestException {
    public BabyBadRequestException(String message) {
        super(message);
    }
}
