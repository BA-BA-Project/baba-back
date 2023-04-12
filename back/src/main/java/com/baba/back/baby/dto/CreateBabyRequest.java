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
public class CreateBabyRequest {

    @NotNull
    private String name;

    @NotNull
    private String relationName;

    @NotNull
    private LocalDate birthday;

    public Baby toEntity(String id, LocalDate now) {
        return Baby.builder()
                .id(id)
                .name(name)
                .birthday(birthday)
                .now(now)
                .build();
    }
}
