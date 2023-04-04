package com.baba.back.content.dto;

import com.baba.back.oauth.dto.MemberResponse;
import java.util.List;

public record LikesResponse(List<IconResponse> likeUsersPreview, List<MemberResponse> likeUsers) {
}
