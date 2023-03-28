package com.baba.back.oauth.dto;

public record MyGroupMemberResponse(String memberId, String name, String relationName, String iconName,
                                    String iconColor) {
}
