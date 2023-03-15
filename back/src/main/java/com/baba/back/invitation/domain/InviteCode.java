package com.baba.back.invitation.domain;

import com.baba.back.invitation.exception.InviteCodeBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class InviteCode {
    public static final int INVITE_CODE_LENGTH = 6;
    public static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
