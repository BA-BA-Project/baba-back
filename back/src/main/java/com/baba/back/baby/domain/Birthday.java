package com.baba.back.baby.domain;

import com.baba.back.baby.exception.BirthdayBadRequestException;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Birthday {
    private static final int VALIDITY_TIME = 2;

    private LocalDate birthday;

    private Birthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public static Birthday of(LocalDate birthday, LocalDate now) {
        validateBirthday(birthday, now);
        return new Birthday(birthday);
    }

    private static void validateBirthday(LocalDate birthday, LocalDate now) {
        if (birthday.isAfter(now.plusYears(VALIDITY_TIME)) || birthday.isBefore(now.minusYears(VALIDITY_TIME))) {
            throw new BirthdayBadRequestException(birthday + "는 " + now + "와 " + VALIDITY_TIME + "이상 차이납니다.");
        }
    }
}
