package com.baba.back.baby.controller;

import com.baba.back.baby.dto.BabiesResponse;
import com.baba.back.baby.dto.BabyNameRequest;
import com.baba.back.baby.dto.CreateBabyRequest;
import com.baba.back.baby.dto.CreateInviteCodeRequest;
import com.baba.back.baby.dto.CreateInviteCodeResponse;
import com.baba.back.baby.dto.InviteCodeRequest;
import com.baba.back.baby.dto.SearchInviteCodeResponse;
import com.baba.back.baby.service.BabyService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.CreatedResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.OkResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아기 관련 API")
@RestController
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @Operation(summary = "아기 추가 요청")
    @CreatedResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/baby")
    public ResponseEntity<Void> createBaby(@Login String memberId, @RequestBody @NotNull CreateBabyRequest request) {
        final String babyId = babyService.createBaby(memberId, request);

        return ResponseEntity.created(URI.create("/baby/" + babyId)).build();
    }

    @Operation(summary = "추가된 전체 아기 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby")
    public ResponseEntity<BabiesResponse> findBabies(@Login String memberId) {
        return ResponseEntity.ok().body(babyService.findBabies(memberId));
    }

    @Operation(summary = "아기 이름 변경 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PatchMapping("/baby/{babyId}")
    public ResponseEntity<Void> updateBabyName(@Login String memberId,
                                               @PathVariable String babyId,
                                               @RequestBody @NotNull BabyNameRequest request) {
        babyService.updateBabyName(memberId, babyId, request.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "초대 코드 생성 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/baby/invite-code")
    public ResponseEntity<CreateInviteCodeResponse> createInviteCode(
            @RequestBody @NotNull CreateInviteCodeRequest request,
            @Login String memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(babyService.createInviteCode(request, memberId));
    }

    @Operation(summary = "초대장 조회 요청")
    @OkResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby/invitation")
    public ResponseEntity<SearchInviteCodeResponse> searchInviteCodeByVisitor(@RequestParam String code) {
        return ResponseEntity.ok().body(babyService.searchInviteCode(code));
    }

    @Operation(summary = "초대코드로 아기 추가 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/baby/code")
    public ResponseEntity<Void> addBabyWithCode(@RequestBody @NotNull InviteCodeRequest request,
                                                @Login String memberId) {
        babyService.addBabyWithCode(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "아기 삭제 요청")
    @ApiResponse(responseCode = "204", description = "NO CONTENT")
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @DeleteMapping("/baby/{babyId}")
    public ResponseEntity<Void> deleteBaby(@PathVariable String babyId,
                                           @Login String memberId) {
        babyService.deleteBaby(memberId, babyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
