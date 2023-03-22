package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class TextLenthBadRequestException extends BadRequestException {
    public TextLenthBadRequestException(String message) {
        super(message);
    }
}
