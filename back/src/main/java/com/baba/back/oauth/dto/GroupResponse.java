package com.baba.back.oauth.dto;

import java.util.List;

public record GroupResponse(String groupName, List<GroupMemberResponse> members) {
}
