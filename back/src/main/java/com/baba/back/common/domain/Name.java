package com.baba.back.common.domain;

import com.baba.back.oauth.exception.NameLengthBadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Name {
    public static final int NAME_MAX_LENGTH = 6;

    @Column(name = "name")
    private String value;

    public Name(String value) {
        validNull(value);
        validName(value);
        this.value = value;
    }

    private void validNull(String name) {
        if (Objects.isNull(name)) {
            throw new NameLengthBadRequestException("이름은 null일 수 없습니다.");
        }
    }

    private void validName(String name) {
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new NameLengthBadRequestException("{ " + name + " } 은 올바르지 않은 이름입니다.");
        }
    }
}
