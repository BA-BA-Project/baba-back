package com.baba.back.baby.controller;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.baby.service.BabyService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아기 관련 API")
@RestController
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @Operation(summary = "기본 설정된 아기의 id 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby/default")
    public ResponseEntity<SearchDefaultBabyResponse> searchDefaultBaby(@Login String memberId) {
        return ResponseEntity.ok().body(babyService.searchDefaultBaby(memberId));
    }
}
