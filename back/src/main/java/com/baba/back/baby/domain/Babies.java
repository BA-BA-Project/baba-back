package com.baba.back.baby.domain;

import com.baba.back.baby.exception.BabiesBadRequestException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Babies {
    private final List<Baby> values;

    public Babies(List<Baby> values) {
        validateEmpty(values);
        this.values = new ArrayList<>(values);
    }

    private void validateEmpty(List<Baby> babies) {
        if (babies.isEmpty()) {
            throw new BabiesBadRequestException("babies의 길이는 0일 수 없습니다.");
        }
    }

    public String getFirstBabyId() {
        return this.values.get(0).getId();
    }
}
