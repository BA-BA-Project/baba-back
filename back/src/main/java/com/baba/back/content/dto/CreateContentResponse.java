package com.baba.back.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateContentResponse(@JsonProperty("isSuccess") boolean success) {
}
