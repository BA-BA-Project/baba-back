package com.baba.back.content.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String memberId,
        String name,
        String relation,
        String iconName,
        String iconColor,
        String tag,
        String comment,
        LocalDateTime createAt) {
}
