package com.baba.back.content.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record AddLikeResponse(@JsonProperty("isLiked") boolean liked) {
}
