package com.baba.back.content.dto;

import java.util.List;

public record CommentsResponse(List<CommentResponse> comments) {
}
