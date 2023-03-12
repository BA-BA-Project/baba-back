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
        String cardStyle) {
}
