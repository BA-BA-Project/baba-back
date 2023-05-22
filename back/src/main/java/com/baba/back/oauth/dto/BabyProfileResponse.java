package com.baba.back.oauth.dto;

import java.time.LocalDate;

public record BabyProfileResponse(LocalDate birthday, FamilyGroupResponse familyGroup, GroupResponse myGroup) {
}
