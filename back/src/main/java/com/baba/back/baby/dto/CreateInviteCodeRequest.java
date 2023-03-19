package com.baba.back.baby.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInviteCodeRequest {

    @NotNull
    private String relationGroup;

    @NotNull
    private String relationName;
}
