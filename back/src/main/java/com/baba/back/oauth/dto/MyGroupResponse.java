package com.baba.back.oauth.dto;

import java.util.List;

public record MyGroupResponse(String groupName, boolean family, List<MyGroupMemberResponse> members) {
}
