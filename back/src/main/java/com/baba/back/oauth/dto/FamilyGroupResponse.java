package com.baba.back.oauth.dto;

import com.baba.back.baby.dto.BabyResponse;
import java.util.List;

public record FamilyGroupResponse(String groupName, List<BabyResponse> babies, List<GroupMemberResponse> members) {
}
