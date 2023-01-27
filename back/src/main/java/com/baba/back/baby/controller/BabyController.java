package com.baba.back.baby.controller;

import com.baba.back.baby.dto.SearchDefaultBabyResponse;
import com.baba.back.baby.service.BabyService;
import com.baba.back.oauth.support.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @GetMapping("/baby/default")
    public ResponseEntity<SearchDefaultBabyResponse> searchDefaultBaby(@Login String memberId) {
        return ResponseEntity.ok().body(babyService.searchDefaultBaby(memberId));
    }
}
