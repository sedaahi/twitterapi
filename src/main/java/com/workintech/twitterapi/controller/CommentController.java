package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.CommentRequest;
import com.workintech.twitterapi.dto.response.CommentResponse;
import com.workintech.twitterapi.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService
    ) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {

        CommentResponse response =
                commentService.createComment(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Bir tweet'e ait yorumları getirir.
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<CommentResponse>> findByTweetId(
            @PathVariable Long tweetId
    ) {

        return ResponseEntity.ok(
                commentService.findByTweetId(tweetId)
        );
    }

    // Bir kullanıcının yaptığı yorumları getirir.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> findByUserId(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                commentService.findByUserId(userId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                commentService.updateComment(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    // Yorum sahibi veya tweet sahibi silebilir.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            Authentication authentication
    ) {

        commentService.deleteComment(
                id,
                authentication.getName()
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}