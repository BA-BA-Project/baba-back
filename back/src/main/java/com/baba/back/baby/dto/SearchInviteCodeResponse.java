package com.baba.back.baby.dto;

import java.util.List;

public record SearchInviteCodeResponse(List<InviteCodeBabyResponse> babies, String relationName, String relationGroup) {
}
