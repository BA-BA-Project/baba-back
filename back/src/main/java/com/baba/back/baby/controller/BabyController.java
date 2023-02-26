package com.baba.back.baby.controller;

import com.baba.back.baby.service.BabyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아기 관련 API")
@RestController
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;
}
