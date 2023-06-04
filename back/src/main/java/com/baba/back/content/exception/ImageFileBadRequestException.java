package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class ImageFileBadRequestException extends BadRequestException {
    public ImageFileBadRequestException(String message) {
        super(message);
    }
}
