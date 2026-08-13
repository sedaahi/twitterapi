package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    // Aynı kullanıcının aynı tweet'i tekrar retweet etmesini kontrol eder.
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);

    // Bir tweet'in toplam retweet sayısını verir.
    long countByTweetId(Long tweetId);

    /**
     *
     Current user + Tweet
     ↓
     Bu kullanıcı bu tweet'i retweet etmiş mi?
     ↓
     Etmişse Retweet kaydını bul
     ↓
     Retweet id'sini frontend'e ver
     */
    Optional<Retweet> findByUserIdAndTweetId(Long userId, Long tweetId);
}