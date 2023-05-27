package com.baba.back.baby.dto;

public record IsMyBabyResponse(String babyId, String groupColor, String name, boolean isMyBaby) implements Comparable<IsMyBabyResponse> {

    @Override
    public int compareTo(IsMyBabyResponse o) {
        return this.name.compareTo(o.name);
    }
}
