package com.baba.back.content.dto;

import jakarta.validation.constraints.NotNull;

public record ContentUpdateTitleAndCardStyleRequest(@NotNull String title, @NotNull String cardStyle) {
}
