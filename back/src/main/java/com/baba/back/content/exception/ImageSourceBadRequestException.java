package com.baba.back.content.exception;

import com.baba.back.exception.BadRequestException;

public class ImageSourceBadRequestException extends BadRequestException {
    public ImageSourceBadRequestException(String message) {
        super(message);
    }
}
