package com.baba.back.oauth.dto;

import java.util.List;

public record GroupResponseWithFamily(String groupName, boolean family, List<GroupMemberResponse> members) {
}
