package com.baba.back.relation.exception;

import com.baba.back.exception.NotFoundException;

public class RelationNotFoundException extends NotFoundException {
    public RelationNotFoundException(String message) {
        super(message);
    }
}
