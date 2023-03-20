package com.baba.back.baby.service;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator implements CodeGenerator {
    private static final Random RANDOM = new Random();

    @Override
    public String generate(int length, String chars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(chars.length());
            stringBuilder.append(chars.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
}
