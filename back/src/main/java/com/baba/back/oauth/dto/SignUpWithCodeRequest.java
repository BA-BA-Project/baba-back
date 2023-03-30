package com.baba.back.oauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpWithCodeRequest {

    @NotNull
    private String inviteCode;

    @NotNull
    private String name;

    @NotNull
    private String iconName;
}
