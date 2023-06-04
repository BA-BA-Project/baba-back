package com.baba.back.oauth.domain.member;

import com.baba.back.oauth.exception.IntroductionLengthBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Introduction {
    public static final int INTRODUCTION_MAX_LENGTH = 100;

    @Column(name = "introduction")
    private String value;

    public Introduction(String value) {
        validNull(value);
        validLength(value);
        this.value = value;
    }

    private void validNull(String introduction) {
        if (Objects.isNull(introduction)) {
            throw new IntroductionLengthBadRequestException("내 소개는 null일 수 없습니다.");
        }
    }

    private void validLength(String introduction) {
        if (introduction.length() > INTRODUCTION_MAX_LENGTH) {
            throw new IntroductionLengthBadRequestException("{ " + introduction + " }" + "의 길이가 올바르지 않습니다.");
        }
    }
}
