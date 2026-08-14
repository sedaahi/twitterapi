package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.LikeRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.service.LikeService;
import com.workintech.twitterapi.service.TweetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class LikeController {

    private final LikeService likeService;
    private final TweetService tweetService;

    public LikeController(
            LikeService likeService,
            TweetService tweetService
    ) {
        this.likeService = likeService;
        this.tweetService = tweetService;
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

        return ResponseEntity
                .noContent()
                .build();
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

        return ResponseEntity
                .noContent()
                .build();
    }

    // Kullanıcının beğendiği tweetleri getirir.
    @GetMapping("/like/user/{userId}")
    public ResponseEntity<List<TweetResponse>> findLikedTweetsByUserId(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                tweetService.findLikedTweetsByUserId(
                        userId,
                        authentication.getName()
                )
        );
    }
}