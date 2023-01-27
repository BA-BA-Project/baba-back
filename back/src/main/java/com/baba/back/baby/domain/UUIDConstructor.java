package com.baba.back.baby.domain;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDConstructor implements IdConstructor {

    @Override
    public String createId() {
        return UUID.randomUUID().toString();
    }
}
