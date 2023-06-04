package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class ContentBadRequestException extends BadRequestException {

    public ContentBadRequestException(String message) {
        super(message);
    }
}
