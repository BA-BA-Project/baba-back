package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class BabiesBadRequestException extends BadRequestException {
    public BabiesBadRequestException(String message) {
        super(message);
    }
}
