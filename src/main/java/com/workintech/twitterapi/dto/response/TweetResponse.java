package com.workintech.twitterapi.dto.response;

import java.time.LocalDateTime;
//tweet sahibinin temel bilgilerine ulaşabilir ama password gibi hassas veriler çıkmaz.
public record TweetResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        UserResponse user,
        long commentCount,
        long likeCount,
        long retweetCount,
        boolean likedByCurrentUser, //kullanıcı toggle like kontrolü için
        boolean retweetedByCurrentUser, //Bu tweet'i ben retweet ettim mi T/F ?  ==> Ettiğim retweet'in DB kaydının ID'si ne?=> currentUserRetweetId
        Long currentUserRetweetId //retweet geri alınırken => DELETE işlemi için

) {
}