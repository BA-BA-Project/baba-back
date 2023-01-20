package com.baba.back.oauth.domain.user;

import com.baba.back.oauth.exception.NameLengthBadRequestException;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Name {
    public static final int NAME_MAX_LENGTH = 6;

    private String name;

    public Name(String name) {
        validNull(name);
        validName(name);
        this.name = name;
    }

    private void validNull(String name) {
        if (Objects.isNull(name)) {
            throw new NameLengthBadRequestException("이름은 null일 수 없습니다.");
        }
    }

    private void validName(String name) {
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new NameLengthBadRequestException(String.format("{" + name + "}" + "은 올바르지 않은 이름입니다.", name.length()));
        }
    }
}
