package com.baba.back.content.controller;

import com.baba.back.content.dto.CreateContentRequest;
import com.baba.back.content.dto.CreateContentResponse;
import com.baba.back.content.service.ContentService;
import com.baba.back.oauth.support.Login;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/album/{babyId}")
    public ResponseEntity<CreateContentResponse> createContent(@ModelAttribute @Valid CreateContentRequest request,
                                                               @Login String memberId,
                                                               @PathVariable("babyId") String babyId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contentService.createContent(request, memberId, babyId));
    }
}
