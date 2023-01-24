package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.MemberJoinRequest;
import com.baba.back.oauth.dto.MemberJoinResponse;
import com.baba.back.oauth.service.MemberService;
import com.baba.back.oauth.support.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<MemberJoinResponse> joinMember(@RequestBody MemberJoinRequest request,
                                                         @Login String memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request, memberId));
    }
}
