package com.workintech.twitterapi.controller;

import com.workintech.twitterapi.dto.request.RetweetRequest;
import com.workintech.twitterapi.service.RetweetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    private final RetweetService retweetService;

    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping
    public ResponseEntity<Void> retweet(
            @Valid @RequestBody RetweetRequest request,
            Authentication authentication
    ) {

        retweetService.retweet(
                request,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetweet(
            @PathVariable Long id,
            Authentication authentication
    ) {

        retweetService.deleteRetweet(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}