package com.workintech.twitterapi.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserResponse user,
        Long tweetId
) {
}