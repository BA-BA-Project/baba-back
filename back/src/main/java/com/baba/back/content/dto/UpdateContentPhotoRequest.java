package com.baba.back.content.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateContentPhotoRequest(@NotNull MultipartFile photo) {
}
