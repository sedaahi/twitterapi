package com.workintech.twitterapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(

        @NotNull(message = "Tweet id boş olamaz.")
        Long tweetId

) {
}