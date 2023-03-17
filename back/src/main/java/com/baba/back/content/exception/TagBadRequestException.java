package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class TagBadRequestException extends BadRequestException {
    public TagBadRequestException(String message) {
        super(message);
    }
}
