package com.baba.back.content.domain.content;

import com.baba.back.content.exception.TitleLengthBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Title {
    public static final int MAX_TITLE_LENGTH = 10;
    private String title;

    public Title(String title) {
        validateNull(title);
        validateLength(title);
        this.title = title;
    }

    private void validateNull(String title) {
        if (Objects.isNull(title)) {
            throw new TitleLengthBadRequestException("제목은 null일 수 없습니다.");
        }
    }

    private void validateLength(String title) {
        if (title.isBlank() || title.length() > MAX_TITLE_LENGTH) {
            throw new TitleLengthBadRequestException(String.format("제목의 길이 {%s}가 올바르지 않습니다.", title.length()));
        }
    }
}
