package com.baba.back.content.dto;

import java.util.List;

public record ContentLikeCommentResponse(
        int likeCount,
        List<String> likeUsers,
        int commentCount,
        String cardStyle,
        List<CommentResponse> comments) {
}
