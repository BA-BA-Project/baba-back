package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.MemberJoinRequest;
import com.baba.back.oauth.dto.MemberJoinResponse;
import com.baba.back.oauth.service.MemberService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.DefaultErrorResponses;
import com.baba.back.swagger.OkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "멤버 생성 요청")
    @DefaultErrorResponses
    @OkResponse
    @PostMapping("/members")
    public ResponseEntity<MemberJoinResponse> joinMember(@RequestBody @Valid MemberJoinRequest request,
                                                         @Login String memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request, memberId));
    }
}
