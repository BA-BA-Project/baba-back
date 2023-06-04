package com.baba.back.baby.dto;

public record BabyResponse(String babyId, String groupColor, String name) implements Comparable<BabyResponse> {

    @Override
    public int compareTo(BabyResponse o) {
        return this.name.compareTo(o.name);
    }
}
