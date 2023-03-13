package com.baba.back.content.dto;

import java.time.LocalDate;

public record ContentResponse(
        Long contentId,
        String ownerName,
        String relation,
        LocalDate date,
        String title,
        boolean like,
        String photo,
        String cardStyle) implements Comparable<ContentResponse> {
    @Override
    public int compareTo(ContentResponse o) {
        return this.date.compareTo(o.date);
    }
}
