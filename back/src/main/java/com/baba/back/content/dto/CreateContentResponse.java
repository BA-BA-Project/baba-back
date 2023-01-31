package com.baba.back.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateContentResponse {
    @JsonProperty("isSuccess")
    private final boolean success;
}
