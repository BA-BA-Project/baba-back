package com.baba.back.oauth.dto;

public record GroupMemberResponse(String memberId, String name, String relationName, String iconName,
                                  String iconColor) {
}
