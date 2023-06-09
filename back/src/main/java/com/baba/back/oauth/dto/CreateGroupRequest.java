package com.baba.back.oauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {

    @NotNull
    private String relationGroup;

    @NotNull
    private String groupColor;
}
