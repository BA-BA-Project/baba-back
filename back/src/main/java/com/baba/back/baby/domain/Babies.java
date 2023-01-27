package com.baba.back.baby.domain;

import com.baba.back.baby.exception.BabiesBadRequestException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Babies {
    private final List<Baby> babies;

    public Babies(List<Baby> babies) {
        validateEmpty(babies);
        this.babies = new ArrayList<>(babies);
    }

    private void validateEmpty(List<Baby> babies) {
        if (babies.isEmpty()) {
            throw new BabiesBadRequestException("babies의 길이는 0일 수 없습니다.");
        }
    }

    public Baby getDefaultBaby() {
        validateEmpty(babies);
        return babies.get(0);
    }

    public List<Baby> getNotDefaultBabies() {
        return Collections.unmodifiableList(babies.subList(1, babies.size()));
    }
}
