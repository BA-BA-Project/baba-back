package com.baba.back.baby.exception;

import com.baba.back.exception.BadRequestException;

public class RelationBadRequestException extends BadRequestException {
    public RelationBadRequestException(String message) {
        super(message);
    }
}
