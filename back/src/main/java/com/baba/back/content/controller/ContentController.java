package com.baba.back.content.controller;

import com.baba.back.content.dto.CommentsResponse;
import com.baba.back.content.dto.ContentsResponse;
import com.baba.back.content.dto.CreateCommentRequest;
import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.LikeContentResponse;
import com.baba.back.content.dto.LikesResponse;
import com.baba.back.content.service.ContentService;
import com.baba.back.oauth.support.Login;
import com.baba.back.swagger.BadRequestResponse;
import com.baba.back.swagger.CreatedResponse;
import com.baba.back.swagger.ForbiddenResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping(value = "/baby/{babyId}/album", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createContent(@ModelAttribute @Valid CreateContentRequest request,
                                              @Login String memberId,
                                              @PathVariable("babyId") String babyId) {
        final Long contentId = contentService.createContent(request, memberId, babyId);
        return ResponseEntity.created(URI.create("/baby/" + babyId + "/album/" + contentId)).body("API 호출 성공");
    }

    @Operation(summary = "좋아요 추가 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/baby/{babyId}/album/{contentId}/like")
    public ResponseEntity<LikeContentResponse> likeContent(@Login String memberId,
                                                           @PathVariable("babyId") String babyId,
                                                           @PathVariable("contentId") Long contentId) {
        return ResponseEntity.status(HttpStatus.OK).body(contentService.likeContent(memberId, babyId, contentId));
    }

    @Operation(summary = "성장 앨범 메인 페이지 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby/{babyId}/album")
    public ResponseEntity<ContentsResponse> getContents(@Login String memberId,
                                                        @PathVariable("babyId") String babyId,
                                                        @RequestParam("year") int year,
                                                        @RequestParam("month") int month) {
        return ResponseEntity.ok(contentService.getContents(memberId, babyId, year, month));
    }

    @Operation(summary = "성장 앨범 댓글 보기 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby/{babyId}/album/{contentId}/comments")
    public ResponseEntity<CommentsResponse> getComments(@Login String memberId,
                                                       @PathVariable("babyId") String babyId,
                                                       @PathVariable("contentId") Long contentId) {
        return ResponseEntity.ok(contentService.getComments(memberId, babyId, contentId));
    }

    @Operation(summary = "성장 앨범 좋아요 보기 조회 요청")
    @OkResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @GetMapping("/baby/{babyId}/album/{contentId}/likes")
    public ResponseEntity<LikesResponse> getlikes(@Login String memberId,
                                                  @PathVariable("babyId") String babyId,
                                                  @PathVariable("contentId") Long contentId) {
        return ResponseEntity.ok(contentService.getLikes(memberId, babyId, contentId));
    }

    @Operation(summary = "성장 앨범 댓글 추가 요청")
    @CreatedResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PostMapping("/baby/{babyId}/album/{contentId}/comment")
    public ResponseEntity<Void> createComment(@Login String memberId,
                                              @PathVariable("babyId") String babyId,
                                              @PathVariable("contentId") Long contentId,
                                              @RequestBody @Valid CreateCommentRequest request) {

        Long commentId = contentService.createComment(memberId, babyId, contentId, request);
        return ResponseEntity.created(URI.create("/baby/" + babyId + "/album/" + contentId + "/comment/" + commentId)).build();
    }

    @Operation(summary = "성장 앨범 댓글 추가 요청")
    @OkResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PatchMapping("/baby/{babyId}/album/{contentId}/title-card")
    public ResponseEntity<Void> updateTitleAndCard(@Login String memberId,
                                              @PathVariable("babyId") String babyId,
                                              @PathVariable("contentId") Long contentId,
                                              @RequestBody @Valid ContentUpdateTitleAndCardStyleRequest request) {

        contentService.updateTitleAndCard(memberId, babyId, contentId, request);
        return ResponseEntity
                .ok()
                .build();
    }

    @Operation(summary = "성장 앨범 댓글 삭제 요청")
    @OkResponse
    @BadRequestResponse
    @UnAuthorizedResponse
    @NotFoundResponse
    @IntervalServerErrorResponse
    @PatchMapping("/baby/{babyId}/album/{contentId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@Login String memberId,
                                                   @PathVariable("babyId") String babyId,
                                                   @PathVariable("contentId") Long contentId,
                                                   @PathVariable("commentId") Long commentId) {

        contentService.deleteComment(memberId, babyId, contentId, commentId);
        return ResponseEntity
                .ok()
                .build();
    }
}
