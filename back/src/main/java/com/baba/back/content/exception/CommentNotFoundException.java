package com.baba.back.content.exception;

import com.baba.back.exception.NotFoundException;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
