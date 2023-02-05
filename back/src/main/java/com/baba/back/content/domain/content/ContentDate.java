package com.baba.back.content.domain.content;

import com.baba.back.content.exception.ContentDateBadRequestException;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ContentDate {
    public static final int LIMIT_YEARS = 2;
    private LocalDate contentDate;

    private ContentDate(LocalDate contentDate) {
        this.contentDate = contentDate;
    }

    public static ContentDate of(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        validateNull(contentDate, now, baseDate);
        validateFuture(contentDate, now);
        validatePast(contentDate, baseDate);
        return new ContentDate(contentDate);
    }

    private static void validateNull(LocalDate contentDate, LocalDate now, LocalDate baseDate) {
        if (Objects.isNull(contentDate) || Objects.isNull(now) || Objects.isNull(baseDate)) {
            throw new ContentDateBadRequestException("날짜는 null일 수 없습니다.");
        }
    }

    private static void validateFuture(LocalDate contentDate, LocalDate now) {
        if (contentDate.isAfter(now)) {
            throw new ContentDateBadRequestException("contentDate는 now보다 미래일 수 없습니다.");
        }
    }

    private static void validatePast(LocalDate contentDate, LocalDate baseDate) {
        if (contentDate.isBefore(baseDate.minusYears(LIMIT_YEARS))) {
            throw new ContentDateBadRequestException(
                    String.format("contentDate는 baseDate의 %d년전까지_유효합니다.", LIMIT_YEARS));
        }
    }
}
