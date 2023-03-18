package com.baba.back.baby.invitation.domain;

import com.baba.back.baby.invitation.exception.InviteCodeBadRequestException;
import com.baba.back.baby.invitation.service.CodeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class InviteCode {
    private static final int INVITE_CODE_LENGTH = 6;
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static InviteCode from(CodeGenerator generator) {
        final String generatedCode = generator.generate(INVITE_CODE_LENGTH, ALLOWED_CHARS);
        return new InviteCode(generatedCode);
    }

    @Column(name = "invite_code")
    private String value;

    public InviteCode(String value) {
        validateNull(value);
        validateLength(value);
        validateCode(value);
        this.value = value;
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new InviteCodeBadRequestException("초대코드는 null일 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() != INVITE_CODE_LENGTH) {
            throw new InviteCodeBadRequestException("초대코드는 " + INVITE_CODE_LENGTH + "자여야 합니다.");
        }
    }

    private void validateCode(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (ALLOWED_CHARS.indexOf(c) == -1) {
                throw new InviteCodeBadRequestException("{" + value + "}는 올바르지 않은 초대코드입니다.");
            }
        }
    }
}
