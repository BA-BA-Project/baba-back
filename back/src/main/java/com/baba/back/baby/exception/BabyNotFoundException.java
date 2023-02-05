package com.baba.back.baby.exception;

import com.baba.back.exception.NotFoundException;

public class BabyNotFoundException extends NotFoundException {
    public BabyNotFoundException(String message) {
        super(message);
    }
}
