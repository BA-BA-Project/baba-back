package com.baba.back.oauth.domain.user;

import com.baba.back.oauth.exception.IntroductionLengthBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Introduction {
    public static final int INTRODUCTION_MAX_LENGTH = 100;

    private String introduction;

    public Introduction(String introduction) {
        validNull(introduction);
        validLength(introduction);
        this.introduction = introduction;
    }

    private void validNull(String introduction) {
        if (Objects.isNull(introduction)) {
            throw new IntroductionLengthBadRequestException("내 소개는 null일 수 없습니다.");
        }
    }

    private void validLength(String introduction) {
        if (introduction.length() > INTRODUCTION_MAX_LENGTH) {
            throw new IntroductionLengthBadRequestException(
                    String.format("{" + introduction + "}" + "의 길이가 올바르지 않습니다.", introduction.length()));
        }
    }
}
