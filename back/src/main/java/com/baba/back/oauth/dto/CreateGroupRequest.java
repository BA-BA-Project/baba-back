package com.baba.back.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    private String relationGroup;
    private String iconColor;
}
