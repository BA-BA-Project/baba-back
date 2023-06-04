package com.baba.back.relation.exception;

import com.baba.back.exception.BadRequestException;

public class RelationGroupBadRequestException extends BadRequestException {
    public RelationGroupBadRequestException(String message) {
        super(message);
    }
}
