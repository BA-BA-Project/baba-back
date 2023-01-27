package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class CardStyleBadRequestException extends BadRequestException {
    public CardStyleBadRequestException(String message) {
        super(message);
    }
}
