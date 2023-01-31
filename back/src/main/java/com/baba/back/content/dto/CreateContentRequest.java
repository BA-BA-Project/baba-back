package com.baba.back.content.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class CreateContentRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    private String title;

    @NotNull
    private MultipartFile photo;

    @NotNull
    private String cardStyle;
}
