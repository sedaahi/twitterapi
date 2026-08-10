package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndTweetId(Long userId, Long tweetId); //POST /like için yazdık

    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId); // POST /dislike =>Çünkü dislike aslında yeni bir entity oluşturmayacak; mevcut Like kaydını bulup silecek.
    long countByTweetId(Long tweetId);
}

/*
POST /like
       ↓
tweet_like tablosuna kayıt

POST /dislike
       ↓
tweet_like tablosundaki kayıt silinir
 */