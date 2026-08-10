package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.LikeRequest;
import com.workintech.twitterapi.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likeTweet(
            @Valid @RequestBody LikeRequest request,
            Authentication authentication
    ) {

        likeService.likeTweet(
                request,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeTweet(
            @Valid @RequestBody LikeRequest request,
            Authentication authentication
    ) {

        likeService.dislikeTweet(
                request,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}