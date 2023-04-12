package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class CommentBadRequestException extends BadRequestException {
    public CommentBadRequestException(String message) {
        super(message);
    }
}
