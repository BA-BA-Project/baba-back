package com.baba.back.content.controller;

import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.service.ContentService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.CreatedResponse;
import com.baba.back.swagger.ForbiddenResponse;
import com.baba.back.swagger.IntervalServerErrorResponse;
import com.baba.back.swagger.NotFoundResponse;
import com.baba.back.swagger.UnAuthorizedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "컨텐츠 관련 API")
@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "성장 앨범 생성 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @ForbiddenResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping(value = "/album/{babyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createContent(@ModelAttribute @Valid CreateContentRequest request,
                                              @Login String memberId,
                                              @PathVariable("babyId") String babyId) {
        final Long contentId = contentService.createContent(request, memberId, babyId);
        return ResponseEntity.created(URI.create("/album/" + babyId + "/" + contentId)).build();
    }

    @Operation(summary = "좋아요 추가 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/album/{babyId}/{contentId}/like")
    public ResponseEntity<LikeContentResponse> likeContent(@Login String memberId,
                                                           @PathVariable("babyId") String babyId,
                                                           @PathVariable("contentId") Long contentId) {
        return ResponseEntity.status(HttpStatus.OK).body(contentService.likeContent(memberId, babyId, contentId));
    }
}
