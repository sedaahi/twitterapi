package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    // Aynı kullanıcının aynı tweet'i tekrar retweet etmesini kontrol eder.
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    // Bir tweet'in toplam retweet sayısını verir.
    long countByTweetId(Long tweetId);
}