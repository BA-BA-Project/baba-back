package com.baba.back.invitation.domain;

import com.baba.back.invitation.exception.InviteCodeBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class InviteCode {
    public static final int INVITE_CODE_LENGTH = 6;
    public static final String UPPERCASE_AND_NUMBER = "[A-Z0-9]{6}";
    private String value;

    public InviteCode(String value) {
        validateNull(value);
        validateCode(value);
        this.value = value;
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new InviteCodeBadRequestException("초대코드는 null일 수 없습니다.");
        }
    }

    private void validateCode(String value) {
        if (value.length() != INVITE_CODE_LENGTH) {
            throw new InviteCodeBadRequestException("초대코드는 " + INVITE_CODE_LENGTH + "자여야 합니다.");
        }

        if (!value.matches(UPPERCASE_AND_NUMBER)) {
            throw new InviteCodeBadRequestException("{" + value + "}는 올바르지 않은 초대코드입니다.");
        }
    }
}
