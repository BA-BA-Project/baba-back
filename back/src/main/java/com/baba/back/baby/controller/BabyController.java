package com.baba.back.baby.controller;

import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.service.BabyService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아기 관련 API")
@RestController
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @Operation(summary = "추가된 전체 아기 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby")
    public BabiesResponse findBabies(@Login String memberId) {
        return babyService.findBabies(memberId);
    }
}
