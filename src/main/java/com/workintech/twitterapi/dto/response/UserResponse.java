package com.workintech.twitterapi.dto.response;
/**Password asla dışarı çıkmayacak*/
public record UserResponse(
        Long id,
        String username,
        String email
) {
}