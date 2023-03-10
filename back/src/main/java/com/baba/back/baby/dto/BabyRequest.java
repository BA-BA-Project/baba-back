package com.baba.back.baby.dto;

import com.baba.back.baby.domain.Baby;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BabyRequest {

    @NotNull
    private String babyName;

    @NotNull
    private LocalDate birthday;

    public Baby toEntity(String id, LocalDate now) {
        return Baby.builder()
                .id(id)
                .name(babyName)
                .birthday(birthday)
                .now(now)
                .build();
    }
}
