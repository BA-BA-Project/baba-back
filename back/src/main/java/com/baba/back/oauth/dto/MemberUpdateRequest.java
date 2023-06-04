package com.baba.back.oauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {

    @NotNull
    private String name;

    @NotNull
    private String introduction;

    @NotNull
    private String iconName;

    @NotNull
    private String iconColor;
}
