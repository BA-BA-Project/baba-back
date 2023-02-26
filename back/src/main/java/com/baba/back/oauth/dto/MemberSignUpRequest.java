package com.baba.back.oauth.dto;

import com.baba.back.baby.dto.BabyRequest;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequest {

    @NotNull
    private String name;

    @NotNull
    private String iconName;

    @NotNull
    private String relationName;

    @NotNull
    private List<BabyRequest> babies;
}
