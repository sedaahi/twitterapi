package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.TweetCreateRequest;
import com.workintech.twitterapi.dto.request.TweetUpdateRequest;
import com.workintech.twitterapi.dto.response.TweetResponse;
import com.workintech.twitterapi.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@SecurityRequirement(name = "bearerAuth")

public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponse> createTweet(
            @Valid @RequestBody TweetCreateRequest request,
            Authentication authentication
    ) {

        TweetResponse response = tweetService.createTweet(
                request,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<List<TweetResponse>> findByUserId(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                tweetService.findByUserId(userId)
        );
    }

    @GetMapping("/findById")
    public ResponseEntity<TweetResponse> findById(
            @RequestParam Long id
    ) {
        return ResponseEntity.ok(
                tweetService.findById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponse> updateTweet(
            @PathVariable Long id,
            @Valid @RequestBody TweetUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                tweetService.updateTweet(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(
            @PathVariable Long id,
            Authentication authentication
    ) {

        tweetService.deleteTweet(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}