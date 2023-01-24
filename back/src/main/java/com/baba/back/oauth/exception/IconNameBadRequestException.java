package com.baba.back.oauth.exception;

import com.baba.back.exception.BadRequestException;

public class IconNameBadRequestException extends BadRequestException {
    public IconNameBadRequestException(String message) {
        super(message);
    }
}
