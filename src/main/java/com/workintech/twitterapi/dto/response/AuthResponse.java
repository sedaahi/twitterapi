package com.workintech.twitterapi.dto.response;
/**Login başarılı olduğunda JWT döneceğiz*/
public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}

/**
 {
 "token": "eyJhbGciOiJIUzI1NiJ9...",
 "tokenType": "Bearer",
 "user": {
 "id": 1,
 "username": "seda",
 "email": "seda@example.com"
 }
 }
 */