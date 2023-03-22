package com.baba.back.content.domain.comment;

import com.baba.back.content.exception.TextLenthBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Text {

    private static final int MAX_TEXT_LENGTH = 250;

    @Column(name = "text")
    private String value;

    public Text(String value) {
        validateNull(value);
        validateLength(value);
        this.value = value;
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new TextLenthBadRequestException("글은 null일 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.isBlank() || value.length() > MAX_TEXT_LENGTH) {
            throw new TextLenthBadRequestException(String.format("댓글의 길이 {%s}가 올바르지 않습니다.", value.length()));
        }
    }
}
