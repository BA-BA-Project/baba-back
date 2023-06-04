package com.baba.back.content.exception;

import com.baba.back.exception.NotFoundException;

public class ContentNotFountException extends NotFoundException {
    public ContentNotFountException(String message) {
        super(message);
    }
}
