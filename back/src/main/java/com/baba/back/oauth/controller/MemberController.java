package com.baba.back.oauth.controller;

import com.baba.back.oauth.dto.BabyProfileResponse;
import com.baba.back.oauth.dto.CreateGroupRequest;
import com.baba.back.oauth.dto.MemberResponse;
import com.baba.back.oauth.dto.MemberSignUpRequest;
import com.baba.back.oauth.dto.MemberSignUpResponse;
import com.baba.back.oauth.dto.MemberUpdateRequest;
import com.baba.back.oauth.dto.MyProfileResponse;
import com.baba.back.oauth.dto.SignUpWithBabyResponse;
import com.baba.back.oauth.dto.SignUpWithCodeRequest;
import com.baba.back.oauth.dto.UpdateGroupMemberRequest;
import com.baba.back.oauth.dto.UpdateGroupRequest;
import com.baba.back.oauth.service.MemberService;
import com.baba.back.oauth.support.Login;
import com.baba.back.oauth.support.SignUp;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.CreatedResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "멤버 생성 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/members/baby")
    public ResponseEntity<MemberSignUpResponse> joinMember(@RequestBody @Valid MemberSignUpRequest request,
                                                           @SignUp String memberId) {
        final SignUpWithBabyResponse response = memberService.signUpWithBaby(request, memberId);
        return ResponseEntity.created(URI.create("/baby/" + response.babyId()))
                .body(response.memberSignUpResponse());
    }

    @Operation(summary = "멤버 정보 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/members")
    public ResponseEntity<MemberResponse> findMember(@Login String memberId) {
        return ResponseEntity.ok(memberService.findMember(memberId));
    }

    @Operation(summary = "마이 프로필 변경 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PutMapping("/members")
    public ResponseEntity<Void> updateMember(@Login String memberId,
                                             @RequestBody @Valid MemberUpdateRequest request) {
        memberService.updateMember(memberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대코드로 멤버 생성 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/members/baby/invite-code")
    public ResponseEntity<MemberSignUpResponse> signUpWithCode(@RequestBody @Valid SignUpWithCodeRequest request,
                                                               @SignUp String memberId) {
        final SignUpWithBabyResponse response = memberService.signUpWithCode(request, memberId);
        return ResponseEntity.created(URI.create("/baby/" + response.babyId()))
                .body(response.memberSignUpResponse());
    }

    @Operation(summary = "그룹 추가 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/members/groups")
    public ResponseEntity<Void> createGroup(@RequestBody @Valid CreateGroupRequest request,
                                            @Login String memberId) {
        memberService.createGroup(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "마이 그룹별 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/members/my-page")
    public ResponseEntity<MyProfileResponse> searchMyProfile(@Login String memberId) {
        return ResponseEntity.ok(memberService.searchMyGroups(memberId));
    }

    @Operation(summary = "다른 아기 프로필 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/members/baby-page/{babyId}")
    public ResponseEntity<BabyProfileResponse> searchBabyProfile(@Login String memberId, @PathVariable String babyId) {
        return ResponseEntity.ok(memberService.searchBabyGroups(memberId, babyId));
    }

    @Operation(summary = "그룹 정보 변경 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PatchMapping("/members/groups")
    public ResponseEntity<Void> updateGroup(@Login String memberId,
                                            @RequestParam("groupName") String groupName,
                                            @RequestBody @Valid UpdateGroupRequest request) {
        memberService.updateGroup(memberId, groupName, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 멤버 정보 변경 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PatchMapping("/members/groups/{groupMemberId}")
    public ResponseEntity<Void> updateGroupMember(@Login String memberId,
                                                  @PathVariable String groupMemberId,
                                                  @RequestBody @Valid UpdateGroupMemberRequest request) {
        memberService.updateGroupMember(memberId, groupMemberId, request);
        return ResponseEntity.ok().build();
    }
}
