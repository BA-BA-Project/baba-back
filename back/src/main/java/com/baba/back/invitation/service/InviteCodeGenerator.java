package com.baba.back.invitation.service;

import static com.baba.back.invitation.domain.InviteCode.ALLOWED_CHARS;
import static com.baba.back.invitation.domain.InviteCode.INVITE_CODE_LENGTH;

import java.util.Random;

public class InviteCodeGenerator {
    private static final Random RANDOM = new Random();

    private InviteCodeGenerator() {
        throw new IllegalStateException(InviteCodeGenerator.class.getSimpleName() + " 기본 생성자 사용 불가");
    }

    public static String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(ALLOWED_CHARS.length());
            stringBuilder.append(ALLOWED_CHARS.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
}
