package com.baba.back.oauth.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgreeTermsRequest {

    @NotNull
    private String socialToken;

    @NotNull
    private List<TermsRequest> terms;
}
