package com.baba.back.content.controller;

import jakarta.validation.constraints.NotNull;

public record UpdateTitleAndCardStyleRequest(@NotNull String title, @NotNull String cardStyle)  {
}
