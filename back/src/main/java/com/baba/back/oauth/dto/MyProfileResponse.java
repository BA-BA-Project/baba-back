package com.baba.back.oauth.dto;

import java.util.List;

public record MyProfileResponse(List<GroupResponseWithFamily> groups) {
}
