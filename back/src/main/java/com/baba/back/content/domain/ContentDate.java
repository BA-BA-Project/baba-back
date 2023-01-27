package com.baba.back.content.domain;

import com.baba.back.content.exception.ContentDateBadRequestException;
import com.baba.back.exception.BadRequestException;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ContentDate {
    private LocalDate contentDate;

    private ContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public static ContentDate of(LocalDate contentDate, LocalDate now, LocalDate birthday) {
        validateNull(contentDate, now, birthday);
        validateFuture(contentDate, now);
        validatePast(contentDate, birthday);
        return new ContentDate(contentDate);
    }

    private static void validateNull(LocalDate contentDate, LocalDate now, LocalDate birthday) {
        if(Objects.isNull(contentDate) || Objects.isNull(now) || Objects.isNull(birthday)) {
            throw new ContentDateBadRequestException("날짜는 null일 수 없습니다.");
        }
    }

    private static void validateFuture(LocalDate contentDate, LocalDate now) {
        if(contentDate.isAfter(now)) {
            throw new ContentDateBadRequestException("contentDate는 now보다 미래일 수 없습니다.");
        }
    }

    private static void validatePast(LocalDate contentDate, LocalDate birthday) {
        if(contentDate.isBefore(birthday.minusYears(2))) {
            throw new ContentDateBadRequestException("contentDate는 birthday의 2년전까지_유효합니다.");
        }
    }
}
