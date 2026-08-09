package com.workintech.twitterapi.dto.response;

import java.time.LocalDateTime;
//tweet sahibinin temel bilgilerine ulaşabilir ama password gibi hassas veriler çıkmaz.
public record TweetResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserResponse user
) {
}