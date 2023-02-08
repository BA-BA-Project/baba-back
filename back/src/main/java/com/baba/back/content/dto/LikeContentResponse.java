package com.baba.back.content.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record LikeContentResponse(@JsonProperty("isLiked") boolean liked) {
}
